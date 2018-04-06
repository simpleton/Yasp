/*
 * Copyright (C) 2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iq80.leveldb.impl;

import android.util.LruCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import org.iq80.leveldb.table.FileChannelTable;
import org.iq80.leveldb.table.MMapTable;
import org.iq80.leveldb.table.Table;
import org.iq80.leveldb.table.UserComparator;
import org.iq80.leveldb.util.Finalizer;
import org.iq80.leveldb.util.InternalTableIterator;
import org.iq80.leveldb.util.Slice;

import static com.simsun.common.base.Utils.requireNonNull;

public class TableCache {
  private final TableLruCache cache;
  private final Finalizer<Table> finalizer = new Finalizer<>(1);

  final UserComparator userComparator;
  final boolean verifyChecksums;
  final File databaseDir;

  public TableCache(
      final File databaseDir,
      final int tableCacheSize,
      final UserComparator userComparator,
      final boolean verifyChecksums) {
    requireNonNull(databaseDir, "databaseName is null");

    cache = new TableLruCache(tableCacheSize);
    this.userComparator = userComparator;
    this.verifyChecksums = verifyChecksums;
    this.databaseDir = databaseDir;
    //.removalListener(new RemovalListener<Long, TableAndFile>() {
    //  @Override
    //  public void onRemoval(RemovalNotification<Long, TableAndFile> notification) {
    //    Table table = notification.getValue().getTable();
    //    finalizer.addCleanup(table, table.closer());
    //  }
    //})
    //.build(new CacheLoader<Long, TableAndFile>() {
    //  @Override
    //  public TableAndFile load(Long fileNumber) throws IOException {
    //    return new TableAndFile(databaseDir, fileNumber, userComparator, verifyChecksums);
    //  }
    //});
  }

  public InternalTableIterator newIterator(FileMetaData file) {
    return newIterator(file.getNumber());
  }

  public InternalTableIterator newIterator(long number) {
    return new InternalTableIterator(getTable(number).iterator());
  }

  public long getApproximateOffsetOf(FileMetaData file, Slice key) {
    return getTable(file.getNumber()).getApproximateOffsetOf(key);
  }

  private Table getTable(long number) {
    TableAndFile tableAndFile = cache.get(number);
    if (tableAndFile == null) {
      try {
        tableAndFile = new TableAndFile(this.databaseDir, number, userComparator, verifyChecksums);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    cache.put(number, tableAndFile);
    return tableAndFile.getTable();
  }

  public void close() {
    cache.evictAll();
    finalizer.destroy();
  }

  public void evict(long number) {
    cache.remove(number);
  }

  private static final class TableAndFile {
    final Table table;

    TableAndFile(
        File databaseDir,
        long fileNumber,
        UserComparator userComparator,
        boolean verifyChecksums) throws IOException {
      String tableFileName = Filename.tableFileName(fileNumber);
      File tableFile = new File(databaseDir, tableFileName);
      FileInputStream fis = new FileInputStream(tableFile);
      FileChannel fileChannel = fis.getChannel();
      if (Iq80DBFactory.USE_MMAP) {
        table = new MMapTable(tableFile.getAbsolutePath(),
            fileChannel,
            userComparator,
            verifyChecksums
        );
      } else {
        table = new FileChannelTable(tableFile.getAbsolutePath(),
            fileChannel,
            userComparator,
            verifyChecksums
        );
      }
    }

    public Table getTable() {
      return table;
    }
  }

  private class TableLruCache extends LruCache<Long, TableAndFile> {

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *     the maximum number of entries in the cache. For all other caches,
     *     this is the maximum sum of the sizes of the entries in this cache.
     */
    public TableLruCache(int maxSize) {
      super(maxSize);
    }

    @Override
    protected void entryRemoved(
        boolean evicted,
        Long key,
        TableAndFile oldValue,
        TableAndFile newValue) {
      super.entryRemoved(evicted, key, oldValue, newValue);
      oldValue.getTable().close();
    }
  }
}
