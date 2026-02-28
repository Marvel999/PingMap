package com.marvel999.pingmap.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.marvel999.pingmap.data.local.entity.SpeedTestEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SpeedTestDao_Impl implements SpeedTestDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SpeedTestEntity> __insertionAdapterOfSpeedTestEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SpeedTestDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSpeedTestEntity = new EntityInsertionAdapter<SpeedTestEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `speed_tests` (`id`,`downloadMbps`,`uploadMbps`,`pingMs`,`jitterMs`,`packetLoss`,`serverLocation`,`isp`,`networkType`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SpeedTestEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getDownloadMbps());
        statement.bindDouble(3, entity.getUploadMbps());
        statement.bindLong(4, entity.getPingMs());
        statement.bindDouble(5, entity.getJitterMs());
        statement.bindLong(6, entity.getPacketLoss());
        if (entity.getServerLocation() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getServerLocation());
        }
        if (entity.getIsp() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getIsp());
        }
        if (entity.getNetworkType() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNetworkType());
        }
        statement.bindLong(10, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM speed_tests WHERE timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM speed_tests";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SpeedTestEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSpeedTestEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOlderThan(final long before, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOlderThan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, before);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOlderThan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SpeedTestEntity>> getRecentResults(final int limit) {
    final String _sql = "SELECT * FROM speed_tests ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"speed_tests"}, new Callable<List<SpeedTestEntity>>() {
      @Override
      @NonNull
      public List<SpeedTestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDownloadMbps = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadMbps");
          final int _cursorIndexOfUploadMbps = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadMbps");
          final int _cursorIndexOfPingMs = CursorUtil.getColumnIndexOrThrow(_cursor, "pingMs");
          final int _cursorIndexOfJitterMs = CursorUtil.getColumnIndexOrThrow(_cursor, "jitterMs");
          final int _cursorIndexOfPacketLoss = CursorUtil.getColumnIndexOrThrow(_cursor, "packetLoss");
          final int _cursorIndexOfServerLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "serverLocation");
          final int _cursorIndexOfIsp = CursorUtil.getColumnIndexOrThrow(_cursor, "isp");
          final int _cursorIndexOfNetworkType = CursorUtil.getColumnIndexOrThrow(_cursor, "networkType");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<SpeedTestEntity> _result = new ArrayList<SpeedTestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SpeedTestEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpDownloadMbps;
            _tmpDownloadMbps = _cursor.getDouble(_cursorIndexOfDownloadMbps);
            final double _tmpUploadMbps;
            _tmpUploadMbps = _cursor.getDouble(_cursorIndexOfUploadMbps);
            final long _tmpPingMs;
            _tmpPingMs = _cursor.getLong(_cursorIndexOfPingMs);
            final double _tmpJitterMs;
            _tmpJitterMs = _cursor.getDouble(_cursorIndexOfJitterMs);
            final int _tmpPacketLoss;
            _tmpPacketLoss = _cursor.getInt(_cursorIndexOfPacketLoss);
            final String _tmpServerLocation;
            if (_cursor.isNull(_cursorIndexOfServerLocation)) {
              _tmpServerLocation = null;
            } else {
              _tmpServerLocation = _cursor.getString(_cursorIndexOfServerLocation);
            }
            final String _tmpIsp;
            if (_cursor.isNull(_cursorIndexOfIsp)) {
              _tmpIsp = null;
            } else {
              _tmpIsp = _cursor.getString(_cursorIndexOfIsp);
            }
            final String _tmpNetworkType;
            if (_cursor.isNull(_cursorIndexOfNetworkType)) {
              _tmpNetworkType = null;
            } else {
              _tmpNetworkType = _cursor.getString(_cursorIndexOfNetworkType);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new SpeedTestEntity(_tmpId,_tmpDownloadMbps,_tmpUploadMbps,_tmpPingMs,_tmpJitterMs,_tmpPacketLoss,_tmpServerLocation,_tmpIsp,_tmpNetworkType,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<SpeedTestEntity> getLatest() {
    final String _sql = "SELECT * FROM speed_tests ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"speed_tests"}, new Callable<SpeedTestEntity>() {
      @Override
      @Nullable
      public SpeedTestEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDownloadMbps = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadMbps");
          final int _cursorIndexOfUploadMbps = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadMbps");
          final int _cursorIndexOfPingMs = CursorUtil.getColumnIndexOrThrow(_cursor, "pingMs");
          final int _cursorIndexOfJitterMs = CursorUtil.getColumnIndexOrThrow(_cursor, "jitterMs");
          final int _cursorIndexOfPacketLoss = CursorUtil.getColumnIndexOrThrow(_cursor, "packetLoss");
          final int _cursorIndexOfServerLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "serverLocation");
          final int _cursorIndexOfIsp = CursorUtil.getColumnIndexOrThrow(_cursor, "isp");
          final int _cursorIndexOfNetworkType = CursorUtil.getColumnIndexOrThrow(_cursor, "networkType");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final SpeedTestEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpDownloadMbps;
            _tmpDownloadMbps = _cursor.getDouble(_cursorIndexOfDownloadMbps);
            final double _tmpUploadMbps;
            _tmpUploadMbps = _cursor.getDouble(_cursorIndexOfUploadMbps);
            final long _tmpPingMs;
            _tmpPingMs = _cursor.getLong(_cursorIndexOfPingMs);
            final double _tmpJitterMs;
            _tmpJitterMs = _cursor.getDouble(_cursorIndexOfJitterMs);
            final int _tmpPacketLoss;
            _tmpPacketLoss = _cursor.getInt(_cursorIndexOfPacketLoss);
            final String _tmpServerLocation;
            if (_cursor.isNull(_cursorIndexOfServerLocation)) {
              _tmpServerLocation = null;
            } else {
              _tmpServerLocation = _cursor.getString(_cursorIndexOfServerLocation);
            }
            final String _tmpIsp;
            if (_cursor.isNull(_cursorIndexOfIsp)) {
              _tmpIsp = null;
            } else {
              _tmpIsp = _cursor.getString(_cursorIndexOfIsp);
            }
            final String _tmpNetworkType;
            if (_cursor.isNull(_cursorIndexOfNetworkType)) {
              _tmpNetworkType = null;
            } else {
              _tmpNetworkType = _cursor.getString(_cursorIndexOfNetworkType);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result = new SpeedTestEntity(_tmpId,_tmpDownloadMbps,_tmpUploadMbps,_tmpPingMs,_tmpJitterMs,_tmpPacketLoss,_tmpServerLocation,_tmpIsp,_tmpNetworkType,_tmpTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
