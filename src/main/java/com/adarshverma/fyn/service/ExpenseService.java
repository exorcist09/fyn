package com.adarshverma.fyn.service;

import com.adarshverma.fyn.dto.ExpenseDTO;
import com.adarshverma.fyn.entity.CategoryEntity;
import com.adarshverma.fyn.entity.ExpenseEntity;
import com.adarshverma.fyn.entity.ProfileEntity;
import com.adarshverma.fyn.repository.CategoryRepository;
import com.adarshverma.fyn.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    //    add expense method --> adding a new Expense to DB
    public ExpenseDTO addExpense(ExpenseDTO dto) {
//        first we are getting the profile of the user & then we are getting the category CategoryRepository where you want to add if this expense and then adding the new expense to ExpenseRepsotioty and then returning back the ExpenseDTO to user
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        ExpenseEntity newExpense = toEntity(dto, profile, category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    //    expenses for current month/or based on the start and end date
    public List<ExpenseDTO> getCurrentMonthExpenseForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    //    delete the expense by ID for current User
    public void deleteExpense(Long expenseId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity entity = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!entity.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException(("Unauthorized to delete this expense"));
        }
        expenseRepository.delete(entity);
    }

//    get latest 5 expenses for current user

    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //    total expense of current user
    public BigDecimal getTotalExpenseForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpensesByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

//    filter expense

    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    //    Notification
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date) {
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDate(profileId, date);
        return list.stream().map(this::toDTO).toList();
    }

    //    helper methods
    public ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category) {
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .category(category)
                .profile(profile)
                .build();
    }

    public ExpenseDTO toDTO(ExpenseEntity entity) {
        return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .amount(entity.getAmount())
                .date(entity.getDate())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

}
