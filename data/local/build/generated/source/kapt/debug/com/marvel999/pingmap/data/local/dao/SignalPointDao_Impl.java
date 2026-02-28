package com.marvel999.pingmap.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.marvel999.pingmap.data.local.entity.SignalPointEntity;
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
public final class SignalPointDao_Impl implements SignalPointDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SignalPointEntity> __insertionAdapterOfSignalPointEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBySession;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SignalPointDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSignalPointEntity = new EntityInsertionAdapter<SignalPointEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `signal_points` (`id`,`sessionId`,`lat`,`lng`,`rssi`,`ssid`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SignalPointEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getSessionId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getSessionId());
        }
        statement.bindDouble(3, entity.getLat());
        statement.bindDouble(4, entity.getLng());
        statement.bindLong(5, entity.getRssi());
        if (entity.getSsid() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getSsid());
        }
        statement.bindLong(7, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfDeleteBySession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM signal_points WHERE sessionId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM signal_points";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SignalPointEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSignalPointEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBySession(final String sessionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBySession.acquire();
        int _argIndex = 1;
        if (sessionId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, sessionId);
        }
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
          __preparedStmtOfDeleteBySession.release(_stmt);
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
  public Flow<List<SignalPointEntity>> getBySession(final String sessionId) {
    final String _sql = "SELECT * FROM signal_points WHERE sessionId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (sessionId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, sessionId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"signal_points"}, new Callable<List<SignalPointEntity>>() {
      @Override
      @NonNull
      public List<SignalPointEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLng = CursorUtil.getColumnIndexOrThrow(_cursor, "lng");
          final int _cursorIndexOfRssi = CursorUtil.getColumnIndexOrThrow(_cursor, "rssi");
          final int _cursorIndexOfSsid = CursorUtil.getColumnIndexOrThrow(_cursor, "ssid");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<SignalPointEntity> _result = new ArrayList<SignalPointEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SignalPointEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSessionId;
            if (_cursor.isNull(_cursorIndexOfSessionId)) {
              _tmpSessionId = null;
            } else {
              _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            }
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLng;
            _tmpLng = _cursor.getDouble(_cursorIndexOfLng);
            final int _tmpRssi;
            _tmpRssi = _cursor.getInt(_cursorIndexOfRssi);
            final String _tmpSsid;
            if (_cursor.isNull(_cursorIndexOfSsid)) {
              _tmpSsid = null;
            } else {
              _tmpSsid = _cursor.getString(_cursorIndexOfSsid);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new SignalPointEntity(_tmpId,_tmpSessionId,_tmpLat,_tmpLng,_tmpRssi,_tmpSsid,_tmpTimestamp);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
