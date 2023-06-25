package cool.oriental.chatcove.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author oriental
 * @since 2023-06-25 01:23:32
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user_info")
public class UserInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId("id")
    private Integer id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 永辉角色
     */
    @TableField("role")
    private String role;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 用户状态
     */
    @TableField("status")
    private Boolean status;

    /**
     * QQ号
     */
    @TableField("tencent_key")
    private String tencentKey;

    /**
     * 微信号
     */
    @TableField("wechat_key")
    private String wechatKey;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
