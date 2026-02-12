package org.wita.erp.infra.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Supplier;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final ProxyManager<byte[]> proxyManager;

    public RateLimitFilter(ProxyManager<byte[]> proxyManager) {
        this.proxyManager = proxyManager;
    }

    private Bucket resolveBucket(String key) {
        Supplier<BucketConfiguration> configSupplier = () ->
                BucketConfiguration.builder()
                        .addLimit(Bandwidth.simple(10, Duration.ofMinutes(1)))
                        .build();

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        return proxyManager.builder().build(keyBytes, configSupplier);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();

        Bucket bucket = resolveBucket("rate-limit:" + ip);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("X-Rate-Limit-Retry-After",
                    String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));
            response.getWriter().write("Too many requests");
        }
    }
}