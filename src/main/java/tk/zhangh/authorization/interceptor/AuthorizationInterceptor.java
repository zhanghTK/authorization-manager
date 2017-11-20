package tk.zhangh.authorization.interceptor;

import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import tk.zhangh.authorization.annoation.Authorization;
import tk.zhangh.authorization.manager.TokenManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

/**
 * 身份验证拦截器
 * Created by ZhangHao on 2017/11/20.
 */
@Setter
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    /**
     * Request中保存当前用户Id字段
     */
    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";

    /**
     * Request中保存Token字段
     */
    private String httpHeaderName = "Authorization";

    /**
     * 身份验证失败后返回错误信息
     */
    private String unauthorizedErrorMessage = "401 unauthorized";

    /**
     * 身份验证失败后返回错误状态码
     */
    private int unauthorizedErrorCode = HttpServletResponse.SC_UNAUTHORIZED;

    /**
     * Token操作
     */
    private TokenManager tokenManager;

    public AuthorizationInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 验证Token
        String token = request.getHeader(httpHeaderName);
        if (token != null && token.length() > 0) {
            String id = tokenManager.getId(token);
            if (id != null) {
                request.setAttribute(CURRENT_USER_ID, id);
                return true;
            }
        }

        // 验证Token失败，返回错误信息
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (isAuthorized(handlerMethod)) {
            response.setStatus(unauthorizedErrorCode);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            BufferedWriter writer =
                    new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
            writer.write(unauthorizedErrorMessage);
            writer.close();
            return false;
        }
        request.setAttribute(CURRENT_USER_ID, null);
        return true;
    }

    private boolean isAuthorized(HandlerMethod handlerMethod) {
        return handlerMethod.getMethod().getAnnotation(Authorization.class) != null ||
                handlerMethod.getBeanType().getAnnotation(Authorization.class) != null;
    }
}
