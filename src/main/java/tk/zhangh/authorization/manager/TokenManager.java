package tk.zhangh.authorization.manager;

/**
 * Token操作接口
 * Created by ZhangHao on 2017/11/20.
 */
public interface TokenManager {
    /**
     * 创建Id与Token记录
     */
    void create(String id, String token);

    /**
     * 根据Token获得Id
     */
    String getId(String token);

    /**
     * 根据Token删除记录
     */
    void deleteByToken(String token);

    /**
     * 根据Id删除记录
     */
    void deleteById(String id);
}
