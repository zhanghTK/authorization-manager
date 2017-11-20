package tk.zhangh.authorization.manager.impl;

import lombok.Data;
import tk.zhangh.authorization.manager.TokenManager;

/**
 * Token操作抽象类
 * Created by ZhangHao on 2017/11/20.
 */
@Data
public abstract class AbstractTokenManager implements TokenManager {

    /**
     * 用户Token是否唯一（是否禁止多端）
     */
    protected boolean singleTokenWithUser = true;
    /**
     * 操作后是否刷新过期时间
     */
    protected boolean flushExpireAfterOperation = true;
    /**
     * Token过期时间
     */
    protected int tokenExpireSeconds = 7 * 24 * 3600;

    @Override
    public void deleteById(String id) {
        if (!singleTokenWithUser) {
            throw new UnsupportedOperationException("不支持非单点登录方式调用");
        }
        deleteSingleRecordById(id);
    }

    /**
     * 根据Id删除唯一记录
     */
    protected abstract void deleteSingleRecordById(String id);

    @Override
    public void create(String id, String token) {
        if (singleTokenWithUser) {
            createSingleRecord(id, token);
        } else {
            createMultipleRecord(id, token);
        }
    }

    /**
     * 创建Id与Token记录，一个Id可以关联多个Token
     */
    protected abstract void createMultipleRecord(String id, String token);

    /**
     * 创建Id与Token记录，一个Id只能关联唯一Token
     */
    protected abstract void createSingleRecord(String id, String token);

    @Override
    public String getId(String token) {
        String id = getIdByToken(token);
        if (id != null && flushExpireAfterOperation) {
            updateExpire(id, token);
        }
        return id;
    }

    /**
     * 根据Token获得Id
     */
    protected abstract String getIdByToken(String token);

    /**
     * 更新过期时间
     */
    protected abstract void updateExpire(String id, String token);
}
