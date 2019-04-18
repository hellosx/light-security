package com.itmuch.lightsecurity.interceptor;

import com.itmuch.lightsecurity.el.PreAuthorizeExpressionRoot;
import com.itmuch.lightsecurity.exception.LightSecurityException;
import com.itmuch.lightsecurity.spec.Spec;
import com.itmuch.lightsecurity.util.PathMatchUtil;
import com.itmuch.lightsecurity.util.SpringElCheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequiredArgsConstructor
public class AuthInterceptor extends HandlerInterceptorAdapter {
    private final List<Spec> specList;
    private final PreAuthorizeExpressionRoot preAuthorizeExpressionRoot;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 当前请求的路径和定义的规则能够匹配
        Boolean checkResult = specList.stream()
                .filter(spec -> PathMatchUtil.match(request, spec.getHttpMethod(), spec.getPath()))
                .findFirst()
                .map(spec -> {
                    String expression = spec.getExpression();
                    return SpringElCheckUtil.check(preAuthorizeExpressionRoot, expression);

                })
                .orElse(true);
        if (!checkResult) {
            throw new LightSecurityException("无权...");
        }

        return true;
    }
}

