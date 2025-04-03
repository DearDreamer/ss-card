# 会员卡系统安全实现文档

## 一、密码传输加密方案

### 1.1 前端实现
前端采用 SHA256 + Base64 的方式处理密码：
```javascript
// 密码处理流程
原始密码 -> SHA256哈希 -> Base64编码 -> 传输到后端

// 示例
原始密码：password123
SHA256后：ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f
Base64后：ZWY5MmI3NzhiYWZlNzcxZTg5MjQ1Yjg5ZWNiYzA4YTQ0YTRlMTY2YzA2NjU5OTExODgxZjM4M2Q0NDczZTk0Zg==
```

### 1.2 后端实现
后端使用 Spring Security + BCrypt 处理密码：
```java
// 密码处理流程
接收Base64密码 -> Base64解码 -> 获取SHA256哈希值 -> BCrypt加密存储
```

## 二、后端实现步骤

### 2.1 添加依赖
```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.9.1</version>
    </dependency>
</dependencies>
```

### 2.2 密码处理工具类
```java
@Component
public class PasswordUtils {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 处理前端传来的密码
     * 1. Base64解码得到SHA256哈希值
     * 2. 使用BCrypt加密存储
     */
    public String processPassword(String base64EncodedPassword) {
        try {
            // Base64解码
            byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedPassword);
            String sha256Hash = new String(decodedBytes);
            
            // BCrypt加密存储
            return passwordEncoder.encode(sha256Hash);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("密码格式不正确");
        }
    }
    
    /**
     * 验证密码
     */
    public boolean verifyPassword(String inputPassword, String storedPassword) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(inputPassword);
            String sha256Hash = new String(decodedBytes);
            return passwordEncoder.matches(sha256Hash, storedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
```

### 2.3 Security配置
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/user/login", "/api/user/register").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080")); // 替换为实际前端域名
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### 2.4 用户服务实现
```java
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordUtils passwordUtils;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 处理密码
        String encodedPassword = passwordUtils.processPassword(request.getPassword());
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        
        return userRepository.save(user);
    }
    
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        
        // 验证密码
        if (!passwordUtils.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 生成JWT令牌
        String token = jwtTokenProvider.generateToken(user);
        
        return new LoginResponse(token);
    }
}
```

## 三、数据库设计

### 3.1 用户表结构
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 四、安全特性

### 4.1 密码安全
1. 密码从不以明文传输
2. 使用SHA256进行前端哈希
3. 使用BCrypt进行后端存储
4. 密码验证过程在服务端完成

### 4.2 传输安全
1. 所有请求通过HTTPS传输
2. 使用JWT进行身份验证
3. 实现了CORS安全配置

### 4.3 存储安全
1. 密码使用BCrypt加密存储
2. BCrypt自动处理盐值
3. 数据库密码即使泄露也难以破解

## 五、注意事项

1. 必须使用HTTPS协议
2. 定期更新JWT密钥
3. 设置合理的Token过期时间
4. 实现登录失败次数限制
5. 记录重要操作日志
6. 定期进行安全审计

## 六、测试用例

### 6.1 注册测试
```http
POST /api/user/register
Content-Type: application/json

{
    "username": "13800138000",
    "password": "ZWY5MmI3NzhiYWZlNzcxZTg5MjQ1Yjg5ZWNiYzA4YTQ0YTRlMTY2YzA2NjU5OTExODgxZjM4M2Q0NDczZTk0Zg=="
}
```

### 6.2 登录测试
```http
POST /api/user/login
Content-Type: application/json

{
    "username": "13800138000",
    "password": "ZWY5MmI3NzhiYWZlNzcxZTg5MjQ1Yjg5ZWNiYzA4YTQ0YTRlMTY2YzA2NjU5OTExODgxZjM4M2Q0NDczZTk0Zg=="
}
```

## 七、错误处理

### 7.1 常见错误码
- 1001: 用户名已存在
- 1002: 用户名或密码错误
- 1003: 密码格式不正确
- 1004: Token已过期
- 1005: Token无效

### 7.2 错误响应格式
```json
{
    "success": false,
    "code": 1001,
    "message": "错误信息",
    "data": null
} 