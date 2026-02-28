package com.marvel999.pingmap.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0010\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\nH\'J\u001e\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\r0\n2\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\'J\u0016\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0011\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0012\u00a8\u0006\u0013"}, d2 = {"Lcom/marvel999/pingmap/data/local/dao/SpeedTestDao;", "", "deleteAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteOlderThan", "before", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLatest", "Lkotlinx/coroutines/flow/Flow;", "Lcom/marvel999/pingmap/data/local/entity/SpeedTestEntity;", "getRecentResults", "", "limit", "", "insert", "entity", "(Lcom/marvel999/pingmap/data/local/entity/SpeedTestEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "local_debug"})
@androidx.room.Dao()
public abstract interface SpeedTestDao {
    
    @androidx.room.Query(value = "SELECT * FROM speed_tests ORDER BY timestamp DESC LIMIT :limit")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.marvel999.pingmap.data.local.entity.SpeedTestEntity>> getRecentResults(int limit);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.marvel999.pingmap.data.local.entity.SpeedTestEntity entity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM speed_tests ORDER BY timestamp DESC LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.marvel999.pingmap.data.local.entity.SpeedTestEntity> getLatest();
    
    @androidx.room.Query(value = "DELETE FROM speed_tests WHERE timestamp < :before")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOlderThan(long before, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM speed_tests")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}