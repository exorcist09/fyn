package com.adarshverma.fyn.service;

import com.adarshverma.fyn.dto.ExpenseDTO;
import com.adarshverma.fyn.entity.ProfileEntity;
import com.adarshverma.fyn.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final ExpenseService expenseService;
    private final EmailService emailService;

    // Injecting the frontend URL
    @Value("${fyn.frontend.url}")
    private String frontEndURL;

    // Sending notification at 10:00 PM (22:00) every day IST
    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started: sendDailyIncomeExpenseReminder()");

        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {
            String body = "Hello " + profile.getFullName() + ",<br><br>"
                    + "This is a reminder to add your Income and Expense for today in Fyn.<br><br>"
                    + "<a href='" + frontEndURL + "' "
                    + "style='display:inline-block; padding:10px 20px; background-color:#000; color:#fff; "
                    + "text-decoration:none; border-radius:5px; font-weight:bold;'>Open Fyn</a><br><br>"
                    + "Best Regards,<br>"
                    + "Fyn Team";

            emailService.sendEmail(
                    profile.getEmail(),
                    "Reminder: Add Your Daily Income and Expense",
                    body
            );
        }
        log.info("Job completed: sendDailyIncomeExpenseReminder()");
    }

    // Sending daily expense summary at 11:00 PM (23:00) every day IST

    @Scheduled(cron = "0 0 23 * * *", zone = "IST")
    public void sendDailyExpenseSummary() {
        log.info("Job started: sendDailyExpenseSummary()");

        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> todayExpense = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now(ZoneId.of("Asia/Kolkata")));
            if (todayExpense != null) {
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse:collapse;width:100%;'>");
                table.append("<tr style='background-color: #f2f2f2;'><th style ='border:1px solid #ddd; padding :8px;'>Amount</th><th style='border:1px solid #ddd padding:8px;'>Category</th>)");
                int i = 1;
                for (ExpenseDTO expense : todayExpense) {
                    table.append("<tr>");
                    table.append("<td style='border:1px solid #ddd; padding:8px;'>").append(i++).append("</td>");
                    table.append("<td style='border:1px solid #ddd; padding:8px;'>").append(expense.getName()).append("</td>");
                    table.append("<td style='border:1px solid #ddd; padding:8px;'>").append(expense.getAmount()).append("</td>");
                    table.append("<td style='border:1px solid #ddd; padding:8px;'>").append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A").append("</td>");
                    table.append("</tr>");
                }
                table.append("</table>");
                String body = "Hello " + profile.getFullName() + ",<br><br>"
                        + "Here is the summary of your expenses for today:<br><br>"
                        + table
                        + "<br>Best Regards,<br>"
                        + "Fyn Team";
                emailService.sendEmail(profile.getEmail(), "Your Daily Expense Summary", body);
            }
        }
        log.info("Job completed: sendDailyExpenseSummary()");
    }
}
