/**
 * Copyright (C) 2013, RedHat, Inc.
 *
 * http://www.redhat.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simsun.lmdb;

/**
 * General exception thrown when error codes are reported by LMDB.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class LmdbException extends RuntimeException {
  /**
   * key/data pair already exists
   */
  public static final int KEYEXIST = -30799;
  /**
   * key/data pair not found (EOF)
   */
  public static final int NOTFOUND = -30798;
  /**
   * Requested page not found - this usually indicates corruption
   */
  public static final int PAGE_NOTFOUND = -30797;
  /**
   * Located page was wrong type
   */
  public static final int CORRUPTED = -30796;
  /**
   * Update of meta page failed, probably I/O error
   */
  public static final int PANIC = -30795;
  /**
   * Environment version mismatch
   */
  public static final int VERSION_MISMATCH = -30794;
  /**
   * File is not a valid LMDB file
   */
  public static final int INVALID = -30793;
  /**
   * Environment mapsize reached
   */
  public static final int MAP_FULL = -30792;
  /**
   * Environment maxdbs reached
   */
  public static final int DBS_FULL = -30791;
  /**
   * Environment maxreaders reached
   */
  public static final int READERS_FULL = -30790;
  /**
   * Too many TLS keys in use - Windows only
   */
  public static final int TLS_FULL = -30789;
  /**
   * Txn has too many dirty pages
   */
  public static final int TXN_FULL = -30788;
  /**
   * Cursor stack too deep - internal error
   */
  public static final int CURSOR_FULL = -30787;
  /**
   * Page has not enough space - internal error
   */
  public static final int PAGE_FULL = -30786;
  /**
   * Database contents grew beyond environment mapsize
   */
  public static final int MAP_RESIZED = -30785;
  /**
   * MDB_INCOMPATIBLE: Operation and DB incompatible, or DB flags changed
   */
  public static final int INCOMPATIBLE = -30784;
  /**
   * Invalid reuse of reader locktable slot
   */
  public static final int BAD_RSLOT = -30783;
  /**
   * Transaction cannot recover - it must be aborted
   */
  public static final int BAD_TXN = -30782;
  /**
   * Unsupported size of key/DB name/data, or wrong DUPFIXED size
   */
  public static final int BAD_VALSIZE = -30781;
  /**
   * The specified DBI was changed unexpectedly
   */
  public static final int BAD_DBI = -30780;
  /**
   * Unexpected problem - txn should abort
   */
  public static final int PROBLEM = -30779;
  /**
   * The last defined error code
   */
  public static final int LAST_ERRCODE = PROBLEM;
  /**
   * Permission denied
   */
  public static final int EACCES = 13;
  /**
   * Table of descriptions for LMDB @ref errors
   */
  public static String[] errMsg = new String[] {
      "MDB_KEYEXIST: Key/data pair already exists", "MDB_NOTFOUND: No matching key/data pair found",
      "MDB_PAGE_NOTFOUND: Requested page not found", "MDB_CORRUPTED: Located page was wrong type",
      "MDB_PANIC: Update of meta page failed or environment had fatal error",
      "MDB_VERSION_MISMATCH: Database environment version mismatch",
      "MDB_INVALID: File is not an LMDB file", "MDB_MAP_FULL: Environment mapsize limit reached",
      "MDB_DBS_FULL: Environment maxdbs limit reached",
      "MDB_READERS_FULL: Environment maxreaders limit reached",
      "MDB_TLS_FULL: Thread-local storage keys full - too many environments open",
      "MDB_TXN_FULL: Transaction has too many dirty pages - transaction too big",
      "MDB_CURSOR_FULL: Internal error - cursor stack limit reached",
      "MDB_PAGE_FULL: Internal error - page has no more space",
      "MDB_MAP_RESIZED: Database contents grew beyond environment mapsize",
      "MDB_INCOMPATIBLE: Operation and DB incompatible, or DB flags changed",
      "MDB_BAD_RSLOT: Invalid reuse of reader locktable slot",
      "MDB_BAD_TXN: Transaction must abort, has a child, or is invalid",
      "MDB_BAD_VALSIZE: Unsupported size of key/DB name/data, or wrong DUPFIXED size",
      "MDB_BAD_DBI: The specified DBI handle was closed/changed unexpectedly",
      "MDB_PROBLEM: Unexpected problem - txn should abort",
  };
  final int errorCode;

  public LmdbException(int errorCode) {
    super(errorCode >= KEYEXIST && errorCode <= LAST_ERRCODE ? errMsg[errorCode] : "");
    this.errorCode = errorCode;
  }

  public LmdbException(String message) {
    super(message);
    errorCode = -1;
  }

  public LmdbException(String message, int errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public int getErrorCode() {
    return errorCode;
  }
}