package com.marvel999.pingmap.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
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
import com.marvel999.pingmap.data.local.entity.DeviceEntity;
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
public final class DeviceDao_Impl implements DeviceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DeviceEntity> __insertionAdapterOfDeviceEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public DeviceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDeviceEntity = new EntityInsertionAdapter<DeviceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `devices` (`macAddress`,`ipAddress`,`hostname`,`manufacturer`,`deviceType`,`isCurrentDevice`,`firstSeen`,`lastSeen`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DeviceEntity entity) {
        if (entity.getMacAddress() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getMacAddress());
        }
        if (entity.getIpAddress() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getIpAddress());
        }
        if (entity.getHostname() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getHostname());
        }
        if (entity.getManufacturer() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getManufacturer());
        }
        if (entity.getDeviceType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDeviceType());
        }
        final int _tmp = entity.isCurrentDevice() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getFirstSeen());
        statement.bindLong(8, entity.getLastSeen());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM devices";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<DeviceEntity> entities,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDeviceEntity.insert(entities);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
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
  public Flow<List<DeviceEntity>> getAll() {
    final String _sql = "SELECT * FROM devices ORDER BY lastSeen DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfHostname = CursorUtil.getColumnIndexOrThrow(_cursor, "hostname");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfDeviceType = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceType");
          final int _cursorIndexOfIsCurrentDevice = CursorUtil.getColumnIndexOrThrow(_cursor, "isCurrentDevice");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "firstSeen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final String _tmpHostname;
            if (_cursor.isNull(_cursorIndexOfHostname)) {
              _tmpHostname = null;
            } else {
              _tmpHostname = _cursor.getString(_cursorIndexOfHostname);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpDeviceType;
            if (_cursor.isNull(_cursorIndexOfDeviceType)) {
              _tmpDeviceType = null;
            } else {
              _tmpDeviceType = _cursor.getString(_cursorIndexOfDeviceType);
            }
            final boolean _tmpIsCurrentDevice;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCurrentDevice);
            _tmpIsCurrentDevice = _tmp != 0;
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            _item = new DeviceEntity(_tmpMacAddress,_tmpIpAddress,_tmpHostname,_tmpManufacturer,_tmpDeviceType,_tmpIsCurrentDevice,_tmpFirstSeen,_tmpLastSeen);
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
  public Object getByMac(final String mac, final Continuation<? super DeviceEntity> $completion) {
    final String _sql = "SELECT * FROM devices WHERE macAddress = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (mac == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, mac);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DeviceEntity>() {
      @Override
      @Nullable
      public DeviceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfHostname = CursorUtil.getColumnIndexOrThrow(_cursor, "hostname");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfDeviceType = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceType");
          final int _cursorIndexOfIsCurrentDevice = CursorUtil.getColumnIndexOrThrow(_cursor, "isCurrentDevice");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "firstSeen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final DeviceEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final String _tmpHostname;
            if (_cursor.isNull(_cursorIndexOfHostname)) {
              _tmpHostname = null;
            } else {
              _tmpHostname = _cursor.getString(_cursorIndexOfHostname);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpDeviceType;
            if (_cursor.isNull(_cursorIndexOfDeviceType)) {
              _tmpDeviceType = null;
            } else {
              _tmpDeviceType = _cursor.getString(_cursorIndexOfDeviceType);
            }
            final boolean _tmpIsCurrentDevice;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCurrentDevice);
            _tmpIsCurrentDevice = _tmp != 0;
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            _result = new DeviceEntity(_tmpMacAddress,_tmpIpAddress,_tmpHostname,_tmpManufacturer,_tmpDeviceType,_tmpIsCurrentDevice,_tmpFirstSeen,_tmpLastSeen);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
