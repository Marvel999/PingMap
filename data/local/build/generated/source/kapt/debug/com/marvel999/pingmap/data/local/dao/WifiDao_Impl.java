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
import com.marvel999.pingmap.data.local.entity.WifiScanEntity;
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
public final class WifiDao_Impl implements WifiDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WifiScanEntity> __insertionAdapterOfWifiScanEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public WifiDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWifiScanEntity = new EntityInsertionAdapter<WifiScanEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `wifi_scans` (`id`,`ssid`,`bssid`,`rssi`,`frequency`,`channel`,`security`,`signalQuality`,`band`,`distanceMeters`,`scannedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WifiScanEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getSsid() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getSsid());
        }
        if (entity.getBssid() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getBssid());
        }
        statement.bindLong(4, entity.getRssi());
        statement.bindLong(5, entity.getFrequency());
        statement.bindLong(6, entity.getChannel());
        if (entity.getSecurity() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getSecurity());
        }
        statement.bindLong(8, entity.getSignalQuality());
        if (entity.getBand() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getBand());
        }
        statement.bindDouble(10, entity.getDistanceMeters());
        statement.bindLong(11, entity.getScannedAt());
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM wifi_scans WHERE scannedAt < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM wifi_scans";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<WifiScanEntity> entities,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWifiScanEntity.insert(entities);
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
  public Flow<List<WifiScanEntity>> getRecentScans(final int limit) {
    final String _sql = "SELECT * FROM wifi_scans ORDER BY scannedAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"wifi_scans"}, new Callable<List<WifiScanEntity>>() {
      @Override
      @NonNull
      public List<WifiScanEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSsid = CursorUtil.getColumnIndexOrThrow(_cursor, "ssid");
          final int _cursorIndexOfBssid = CursorUtil.getColumnIndexOrThrow(_cursor, "bssid");
          final int _cursorIndexOfRssi = CursorUtil.getColumnIndexOrThrow(_cursor, "rssi");
          final int _cursorIndexOfFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "frequency");
          final int _cursorIndexOfChannel = CursorUtil.getColumnIndexOrThrow(_cursor, "channel");
          final int _cursorIndexOfSecurity = CursorUtil.getColumnIndexOrThrow(_cursor, "security");
          final int _cursorIndexOfSignalQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "signalQuality");
          final int _cursorIndexOfBand = CursorUtil.getColumnIndexOrThrow(_cursor, "band");
          final int _cursorIndexOfDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceMeters");
          final int _cursorIndexOfScannedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scannedAt");
          final List<WifiScanEntity> _result = new ArrayList<WifiScanEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WifiScanEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSsid;
            if (_cursor.isNull(_cursorIndexOfSsid)) {
              _tmpSsid = null;
            } else {
              _tmpSsid = _cursor.getString(_cursorIndexOfSsid);
            }
            final String _tmpBssid;
            if (_cursor.isNull(_cursorIndexOfBssid)) {
              _tmpBssid = null;
            } else {
              _tmpBssid = _cursor.getString(_cursorIndexOfBssid);
            }
            final int _tmpRssi;
            _tmpRssi = _cursor.getInt(_cursorIndexOfRssi);
            final int _tmpFrequency;
            _tmpFrequency = _cursor.getInt(_cursorIndexOfFrequency);
            final int _tmpChannel;
            _tmpChannel = _cursor.getInt(_cursorIndexOfChannel);
            final String _tmpSecurity;
            if (_cursor.isNull(_cursorIndexOfSecurity)) {
              _tmpSecurity = null;
            } else {
              _tmpSecurity = _cursor.getString(_cursorIndexOfSecurity);
            }
            final int _tmpSignalQuality;
            _tmpSignalQuality = _cursor.getInt(_cursorIndexOfSignalQuality);
            final String _tmpBand;
            if (_cursor.isNull(_cursorIndexOfBand)) {
              _tmpBand = null;
            } else {
              _tmpBand = _cursor.getString(_cursorIndexOfBand);
            }
            final double _tmpDistanceMeters;
            _tmpDistanceMeters = _cursor.getDouble(_cursorIndexOfDistanceMeters);
            final long _tmpScannedAt;
            _tmpScannedAt = _cursor.getLong(_cursorIndexOfScannedAt);
            _item = new WifiScanEntity(_tmpId,_tmpSsid,_tmpBssid,_tmpRssi,_tmpFrequency,_tmpChannel,_tmpSecurity,_tmpSignalQuality,_tmpBand,_tmpDistanceMeters,_tmpScannedAt);
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
