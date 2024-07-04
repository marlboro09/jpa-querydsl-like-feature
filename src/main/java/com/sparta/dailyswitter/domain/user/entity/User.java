package com.sparta.dailyswitter.domain.user.entity;

import com.sparta.dailyswitter.common.util.Timestamped;
import com.sparta.dailyswitter.domain.user.dto.UserInfoRequestDto;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users") // 테이블 이름을 'users'로 수정
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "login_id", nullable = false, unique = true)
    private String userId;

    @NotBlank
    @Column(name = "user_name")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    private String provider;

    private String providerId;

    @Column(name = "intro")
    private String intro;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "access_token")  // 액세스 토큰 필드 추가
    private String accessToken;

    @Builder.Default
    @Column(name = "is_blocked")
    private boolean isBlocked = false;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(unique = true)
    private String kakaoId;

    @Column(unique = true)
    private String naverId;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "password_history", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> passwordHistory = new ArrayList<>();

    public User(String username, String password, String email, UserRoleEnum role, String kakaoId,
        String naverId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.kakaoId = kakaoId;
        this.naverId = naverId;
    }

    public void updateUserInfo(UserInfoRequestDto userInfoRequestDto) {
        this.username = updateField(userInfoRequestDto.getUsername(), username);
        this.intro = updateField(userInfoRequestDto.getIntro(), intro);
    }

    public void updatePassword(String newPassword) {
        if (this.passwordHistory.size() >= 4) {
            this.passwordHistory.remove(0);
        }
        this.passwordHistory.add(this.password);
        this.password = newPassword;
    }

    public boolean isPasswordInHistory(String password, PasswordEncoder passwordEncoder) {
        for (String oldPassword : passwordHistory) {
            if (passwordEncoder.matches(password, oldPassword)) {
                return true;
            }
        }
        return false;
    }

    public boolean isExist() {
        return this.role != UserRoleEnum.WITHDRAW;
    }

    public void updateRefresh(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void updateStatusSignout() {
        this.role = UserRoleEnum.WITHDRAW;
    }

    public void updateStatus(UserRoleEnum role) {
        this.role = role;
    }

    public void toggleBlock() {
        this.isBlocked = !this.isBlocked;
    }

    private String updateField(String newValue, String currentValue) {
        return newValue == null || newValue.isBlank() ? currentValue : newValue;
    }

    public void setKakaoId(String kakaoId) {
        this.kakaoId = kakaoId;
    }

    public void setNaverId(String naverId) {
        this.naverId = naverId;
    }
}
