package com.shanzhu.beadhouse.common.config.security.filter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.shanzhu.beadhouse.common.config.tenant.TenantContextHolder;
import com.shanzhu.beadhouse.common.constant.Constant;
import com.shanzhu.beadhouse.common.constant.ExceptionEnum;
import com.shanzhu.beadhouse.common.util.*;
import com.shanzhu.beadhouse.entity.vo.LoginUserVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private CommonUtil commonUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 打印访问日志
            log.info(request.getRequestURL().toString());
            // 获取jwt
            String jwt = request.getHeader("token");
            // 获取请求路径
            String requestURI = request.getRequestURI();
            // 验证url是否在白名单之内
            boolean ignore = commonUtil.existFlag(commonUtil.getIgnoreUrlArray(), requestURI);
            // jwt为空 且 url在白名单之内
            if (!StringUtils.hasText(jwt) && ignore) {
                filterChain.doFilter(request, response);
                return;
            }
            // （jwt为空 但 url未在白名单之内） 或 （jwt不为空 且 url在白名单之内）
            boolean accessError = (!StringUtils.hasText(jwt) && !ignore) || (StringUtils.hasText(jwt) && ignore);
            if (accessError) {
                ResponseUtil.exceptionEnumOut(response, ExceptionEnum.ACCESS_ERROR);
                return;
            }
            // jwt不为空 且 url未在白名单之内
            // 解析jwt获取用户id
            String id;
            String tenantId = "1";
            try {
                Jws<Claims> claimsJws = JwtUtil.parseJwt(jwt);
                id = claimsJws.getBody().get("id").toString();
                Object tenantIdObj = claimsJws.getBody().get("tenantId");
                if (tenantIdObj != null) {
                    tenantId = tenantIdObj.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // token解析出错（非法token/token过期）
                ResponseUtil.exceptionEnumOut(response, ExceptionEnum.TOKEN_ERROR);
                return;
            }
            // 从redis获取用户信息
            String loginRedisKey = Constant.LOGIN_REDIS + tenantId + ":" + id;
            Object redisUserData = redisUtil.getCacheObject(loginRedisKey);
            // 兼容旧登录缓存key
            if (ObjUtil.isEmpty(redisUserData)) {
                redisUserData = redisUtil.getCacheObject(Constant.LOGIN_REDIS + id);
            }
            // 获取redis数据为空
            if (ObjUtil.isEmpty(redisUserData)) {
                ResponseUtil.exceptionEnumOut(response, ExceptionEnum.TOKEN_ERROR);
                return;
            }
            LoginUserVo loginUserVo = BeanUtil.toBean(redisUserData, LoginUserVo.class);
            TenantContextHolder.setTenantId(loginUserVo.getTenantId());
            TenantContextHolder.setTenantCode(loginUserVo.getTenantCode());
            TenantContextHolder.setPlatformAdmin(Boolean.TRUE.equals(loginUserVo.getPlatformAdmin()));
            // 获取权限列表并转换
            List<SimpleGrantedAuthority> authorityList = loginUserVo.getAuthUrlList().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            // 认证
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUserVo, null, authorityList);
            // 存入上下文
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            // 放行
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }
}
