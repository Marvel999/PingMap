package com.marvel999.pingmap.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006H\'J\u0018\u0010\t\u001a\u0004\u0018\u00010\b2\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001c\u0010\r\u001a\u00020\u00032\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00a7@\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006\u0010"}, d2 = {"Lcom/marvel999/pingmap/data/local/dao/DeviceDao;", "", "deleteAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/marvel999/pingmap/data/local/entity/DeviceEntity;", "getByMac", "mac", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertAll", "entities", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "local_debug"})
@androidx.room.Dao()
public abstract interface DeviceDao {
    
    @androidx.room.Query(value = "SELECT * FROM devices ORDER BY lastSeen DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.marvel999.pingmap.data.local.entity.DeviceEntity>> getAll();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.marvel999.pingmap.data.local.entity.DeviceEntity> entities, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM devices WHERE macAddress = :mac LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByMac(@org.jetbrains.annotations.NotNull()
    java.lang.String mac, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.marvel999.pingmap.data.local.entity.DeviceEntity> $completion);
    
    @androidx.room.Query(value = "DELETE FROM devices")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}