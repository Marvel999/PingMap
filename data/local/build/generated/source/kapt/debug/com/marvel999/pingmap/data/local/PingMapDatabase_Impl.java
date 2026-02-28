package com.marvel999.pingmap.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.marvel999.pingmap.data.local.dao.DeviceDao;
import com.marvel999.pingmap.data.local.dao.DeviceDao_Impl;
import com.marvel999.pingmap.data.local.dao.SignalPointDao;
import com.marvel999.pingmap.data.local.dao.SignalPointDao_Impl;
import com.marvel999.pingmap.data.local.dao.SpeedTestDao;
import com.marvel999.pingmap.data.local.dao.SpeedTestDao_Impl;
import com.marvel999.pingmap.data.local.dao.WifiDao;
import com.marvel999.pingmap.data.local.dao.WifiDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PingMapDatabase_Impl extends PingMapDatabase {
  private volatile WifiDao _wifiDao;

  private volatile SpeedTestDao _speedTestDao;

  private volatile DeviceDao _deviceDao;

  private volatile SignalPointDao _signalPointDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `wifi_scans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `ssid` TEXT NOT NULL, `bssid` TEXT NOT NULL, `rssi` INTEGER NOT NULL, `frequency` INTEGER NOT NULL, `channel` INTEGER NOT NULL, `security` TEXT NOT NULL, `signalQuality` INTEGER NOT NULL, `band` TEXT NOT NULL, `distanceMeters` REAL NOT NULL, `scannedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `speed_tests` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `downloadMbps` REAL NOT NULL, `uploadMbps` REAL NOT NULL, `pingMs` INTEGER NOT NULL, `jitterMs` REAL NOT NULL, `packetLoss` INTEGER NOT NULL, `serverLocation` TEXT NOT NULL, `isp` TEXT NOT NULL, `networkType` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `devices` (`macAddress` TEXT NOT NULL, `ipAddress` TEXT NOT NULL, `hostname` TEXT, `manufacturer` TEXT, `deviceType` TEXT NOT NULL, `isCurrentDevice` INTEGER NOT NULL, `firstSeen` INTEGER NOT NULL, `lastSeen` INTEGER NOT NULL, PRIMARY KEY(`macAddress`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `signal_points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` TEXT NOT NULL, `lat` REAL NOT NULL, `lng` REAL NOT NULL, `rssi` INTEGER NOT NULL, `ssid` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6330870a7c75d437ea8a11538684aa9d')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `wifi_scans`");
        db.execSQL("DROP TABLE IF EXISTS `speed_tests`");
        db.execSQL("DROP TABLE IF EXISTS `devices`");
        db.execSQL("DROP TABLE IF EXISTS `signal_points`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsWifiScans = new HashMap<String, TableInfo.Column>(11);
        _columnsWifiScans.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWifiScans.put("ssid", new TableInfo.Column("ssid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWifiScans.put("bssid", new TableInfo.Column("bssid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWifiScans.put("rssi", new TableInfo.Column("rssi", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWifiScans.put("frequency", new TableInfo.Column("frequency", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWifiScans.put("channel", new TableInfo.Column("channel", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWifiScans.put("security", new TableInfo.Column("security", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWifiScans.put("signalQuality", new TableInfo.Column("signalQuality", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWifiScans.put("band", new TableInfo.Column("band", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWifiScans.put("distanceMeters", new TableInfo.Column("distanceMeters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWifiScans.put("scannedAt", new TableInfo.Column("scannedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWifiScans = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWifiScans = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWifiScans = new TableInfo("wifi_scans", _columnsWifiScans, _foreignKeysWifiScans, _indicesWifiScans);
        final TableInfo _existingWifiScans = TableInfo.read(db, "wifi_scans");
        if (!_infoWifiScans.equals(_existingWifiScans)) {
          return new RoomOpenHelper.ValidationResult(false, "wifi_scans(com.marvel999.pingmap.data.local.entity.WifiScanEntity).\n"
                  + " Expected:\n" + _infoWifiScans + "\n"
                  + " Found:\n" + _existingWifiScans);
        }
        final HashMap<String, TableInfo.Column> _columnsSpeedTests = new HashMap<String, TableInfo.Column>(10);
        _columnsSpeedTests.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSpeedTests.put("downloadMbps", new TableInfo.Column("downloadMbps", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSpeedTests.put("uploadMbps", new TableInfo.Column("uploadMbps", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSpeedTests.put("pingMs", new TableInfo.Column("pingMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSpeedTests.put("jitterMs", new TableInfo.Column("jitterMs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSpeedTests.put("packetLoss", new TableInfo.Column("packetLoss", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSpeedTests.put("serverLocation", new TableInfo.Column("serverLocation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSpeedTests.put("isp", new TableInfo.Column("isp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSpeedTests.put("networkType", new TableInfo.Column("networkType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSpeedTests.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSpeedTests = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSpeedTests = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSpeedTests = new TableInfo("speed_tests", _columnsSpeedTests, _foreignKeysSpeedTests, _indicesSpeedTests);
        final TableInfo _existingSpeedTests = TableInfo.read(db, "speed_tests");
        if (!_infoSpeedTests.equals(_existingSpeedTests)) {
          return new RoomOpenHelper.ValidationResult(false, "speed_tests(com.marvel999.pingmap.data.local.entity.SpeedTestEntity).\n"
                  + " Expected:\n" + _infoSpeedTests + "\n"
                  + " Found:\n" + _existingSpeedTests);
        }
        final HashMap<String, TableInfo.Column> _columnsDevices = new HashMap<String, TableInfo.Column>(8);
        _columnsDevices.put("macAddress", new TableInfo.Column("macAddress", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("ipAddress", new TableInfo.Column("ipAddress", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("hostname", new TableInfo.Column("hostname", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("manufacturer", new TableInfo.Column("manufacturer", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("deviceType", new TableInfo.Column("deviceType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("isCurrentDevice", new TableInfo.Column("isCurrentDevice", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("firstSeen", new TableInfo.Column("firstSeen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("lastSeen", new TableInfo.Column("lastSeen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDevices = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDevices = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDevices = new TableInfo("devices", _columnsDevices, _foreignKeysDevices, _indicesDevices);
        final TableInfo _existingDevices = TableInfo.read(db, "devices");
        if (!_infoDevices.equals(_existingDevices)) {
          return new RoomOpenHelper.ValidationResult(false, "devices(com.marvel999.pingmap.data.local.entity.DeviceEntity).\n"
                  + " Expected:\n" + _infoDevices + "\n"
                  + " Found:\n" + _existingDevices);
        }
        final HashMap<String, TableInfo.Column> _columnsSignalPoints = new HashMap<String, TableInfo.Column>(7);
        _columnsSignalPoints.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSignalPoints.put("sessionId", new TableInfo.Column("sessionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSignalPoints.put("lat", new TableInfo.Column("lat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSignalPoints.put("lng", new TableInfo.Column("lng", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSignalPoints.put("rssi", new TableInfo.Column("rssi", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSignalPoints.put("ssid", new TableInfo.Column("ssid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSignalPoints.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSignalPoints = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSignalPoints = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSignalPoints = new TableInfo("signal_points", _columnsSignalPoints, _foreignKeysSignalPoints, _indicesSignalPoints);
        final TableInfo _existingSignalPoints = TableInfo.read(db, "signal_points");
        if (!_infoSignalPoints.equals(_existingSignalPoints)) {
          return new RoomOpenHelper.ValidationResult(false, "signal_points(com.marvel999.pingmap.data.local.entity.SignalPointEntity).\n"
                  + " Expected:\n" + _infoSignalPoints + "\n"
                  + " Found:\n" + _existingSignalPoints);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "6330870a7c75d437ea8a11538684aa9d", "7a74db3b6fee68f41434c65f469cd08e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "wifi_scans","speed_tests","devices","signal_points");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `wifi_scans`");
      _db.execSQL("DELETE FROM `speed_tests`");
      _db.execSQL("DELETE FROM `devices`");
      _db.execSQL("DELETE FROM `signal_points`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(WifiDao.class, WifiDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SpeedTestDao.class, SpeedTestDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DeviceDao.class, DeviceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SignalPointDao.class, SignalPointDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public WifiDao wifiDao() {
    if (_wifiDao != null) {
      return _wifiDao;
    } else {
      synchronized(this) {
        if(_wifiDao == null) {
          _wifiDao = new WifiDao_Impl(this);
        }
        return _wifiDao;
      }
    }
  }

  @Override
  public SpeedTestDao speedTestDao() {
    if (_speedTestDao != null) {
      return _speedTestDao;
    } else {
      synchronized(this) {
        if(_speedTestDao == null) {
          _speedTestDao = new SpeedTestDao_Impl(this);
        }
        return _speedTestDao;
      }
    }
  }

  @Override
  public DeviceDao deviceDao() {
    if (_deviceDao != null) {
      return _deviceDao;
    } else {
      synchronized(this) {
        if(_deviceDao == null) {
          _deviceDao = new DeviceDao_Impl(this);
        }
        return _deviceDao;
      }
    }
  }

  @Override
  public SignalPointDao signalPointDao() {
    if (_signalPointDao != null) {
      return _signalPointDao;
    } else {
      synchronized(this) {
        if(_signalPointDao == null) {
          _signalPointDao = new SignalPointDao_Impl(this);
        }
        return _signalPointDao;
      }
    }
  }
}
