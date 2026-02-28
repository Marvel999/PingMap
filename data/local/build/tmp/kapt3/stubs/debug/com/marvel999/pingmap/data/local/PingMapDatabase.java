package com.marvel999.pingmap.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&\u00a8\u0006\u000b"}, d2 = {"Lcom/marvel999/pingmap/data/local/PingMapDatabase;", "Landroidx/room/RoomDatabase;", "()V", "deviceDao", "Lcom/marvel999/pingmap/data/local/dao/DeviceDao;", "signalPointDao", "Lcom/marvel999/pingmap/data/local/dao/SignalPointDao;", "speedTestDao", "Lcom/marvel999/pingmap/data/local/dao/SpeedTestDao;", "wifiDao", "Lcom/marvel999/pingmap/data/local/dao/WifiDao;", "local_debug"})
@androidx.room.Database(entities = {com.marvel999.pingmap.data.local.entity.WifiScanEntity.class, com.marvel999.pingmap.data.local.entity.SpeedTestEntity.class, com.marvel999.pingmap.data.local.entity.DeviceEntity.class, com.marvel999.pingmap.data.local.entity.SignalPointEntity.class}, version = 1, exportSchema = false)
public abstract class PingMapDatabase extends androidx.room.RoomDatabase {
    
    public PingMapDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.marvel999.pingmap.data.local.dao.WifiDao wifiDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.marvel999.pingmap.data.local.dao.SpeedTestDao speedTestDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.marvel999.pingmap.data.local.dao.DeviceDao deviceDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.marvel999.pingmap.data.local.dao.SignalPointDao signalPointDao();
}