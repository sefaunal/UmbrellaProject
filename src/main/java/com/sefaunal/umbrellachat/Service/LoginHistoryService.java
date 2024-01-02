package com.sefaunal.umbrellachat.Service;

import com.sefaunal.umbrellachat.Model.LoginHistory;
import com.sefaunal.umbrellachat.Model.User;
import com.sefaunal.umbrellachat.Repository.LoginHistoryRepository;
import com.sefaunal.umbrellachat.Util.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author github.com/sefaunal
 * @since 2023-12-04
 */
@Service
@RequiredArgsConstructor
public class LoginHistoryService {
    private final LoginHistoryRepository loginHistoryRepository;

    private final UserService userService;

    public void saveLoginHistory(HttpServletRequest request, String userMail) {
        User user = userService.findUserByMail(userMail);

        LoginHistory loginHistory = new LoginHistory();

        loginHistory.setUserID(user.getID());
        loginHistory.setTimestamp(LocalDateTime.now());
        loginHistory.setEnvironment(CommonUtils.getUserEnvironment(request));
        loginHistory.setIPAddress(CommonUtils.getIpAddress(request));

        loginHistoryRepository.save(loginHistory);
    }

    public Page<LoginHistory> getLoginHistory(Pageable pageable) {
        User user = userService.findUserByMail(CommonUtils.getUserInfo());

        return loginHistoryRepository.getLoginRecordsByUserID(user.getID(), pageable);
    }
}
