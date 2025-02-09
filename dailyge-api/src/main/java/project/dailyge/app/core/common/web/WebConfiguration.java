package project.dailyge.app.core.common.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import project.dailyge.app.common.auth.TokenProvider;
import project.dailyge.app.core.common.auth.AuthArgumentResolver;
import project.dailyge.app.core.common.auth.LoginInterceptor;
import project.dailyge.core.cache.user.UserCacheReadService;

import java.util.List;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final String env;
    private final UserCacheReadService userCacheReadService;
    private final TokenProvider tokenProvider;
    private final LoginInterceptor loginInterceptor;
    private final CouponApplyInterceptor couponApplyInterceptor;

    public WebConfiguration(
        @Value("${env}") final String env,
        final UserCacheReadService userCacheReadService,
        final TokenProvider tokenProvider,
        final LoginInterceptor loginInterceptor,
        final CouponApplyInterceptor couponApplyInterceptor
    ) {
        this.env = env;
        this.userCacheReadService = userCacheReadService;
        this.tokenProvider = tokenProvider;
        this.loginInterceptor = loginInterceptor;
        this.couponApplyInterceptor = couponApplyInterceptor;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthArgumentResolver(env, userCacheReadService, tokenProvider));
        resolvers.add(new CursorPagingArgumentResolver());
        resolvers.add(new OffsetPagingArgumentResolver());
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/api/login");
        registry.addInterceptor(couponApplyInterceptor).addPathPatterns("/api/coupons");
    }

    @Bean
    public RequestContextFilter requestContextFilter() {
        return new RequestContextFilter();
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/docs/**")
            .addResourceLocations("classpath:/static/docs/");
    }
}
