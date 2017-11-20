package tk.zhangh.authorization.repository;

/**
 * 用户模型Repository接口
 *
 * @see tk.zhangh.authorization.resolver.CurrentUserMethodArgumentResolver
 * Created by ZhangHao on 2017/11/20.
 */
public interface UserRepository<T> {
    T getCurrentUser(String id);
}
