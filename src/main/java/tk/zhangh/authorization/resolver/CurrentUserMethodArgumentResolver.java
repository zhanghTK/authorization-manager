package tk.zhangh.authorization.resolver;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import tk.zhangh.authorization.annoation.CurrentUser;
import tk.zhangh.authorization.interceptor.AuthorizationInterceptor;
import tk.zhangh.authorization.repository.UserRepository;

/**
 * 解析Controller方法中@CurrentUser修饰的参数
 * Created by ZhangHao on 2017/11/20.
 */
@Data
@AllArgsConstructor
public class CurrentUserMethodArgumentResolver<T> implements HandlerMethodArgumentResolver {

    private Class<T> userModelClass;

    private UserRepository<T> userModelRepository;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(userModelClass)
                && methodParameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory)
            throws Exception {
        Object object = nativeWebRequest.getAttribute(AuthorizationInterceptor.CURRENT_USER_ID,
                RequestAttributes.SCOPE_REQUEST);
        if (object == null) {
            return null;
        }
        String currentUserId = String.valueOf(object);
        T currentUser = userModelRepository.getCurrentUser(currentUserId);
        if (currentUser == null) {
            throw new MissingServletRequestPartException(AuthorizationInterceptor.CURRENT_USER_ID);
        }
        return currentUser;
    }
}
