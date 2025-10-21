// java
package com.adarshverma.fyn.service;

import com.adarshverma.fyn.dto.AuthDTO;
import com.adarshverma.fyn.dto.ProfileDTO;
import com.adarshverma.fyn.entity.ProfileEntity;
import com.adarshverma.fyn.repository.ProfileRepository;
import com.adarshverma.fyn.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    // Repository ko inject kar raha hai via Lombok constructor
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtutil;

    @Value("${fyn.activation.url}")
    private String activationUrl;


    // Profile register karne ka main method
    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        // DTO se entity banayi ja rahi hai taaki DB me save kiya ja sake
        ProfileEntity newProfile = toEntity(profileDTO);
//        activation token set karna - unique token generate kar raha hai
        newProfile.setActivationToken(UUID.randomUUID().toString());

//        ab is naye profile ko database me save kar rahe hain
        newProfile = profileRepository.save(newProfile);

//        send activation email to the user defined email
        String activationLink = activationUrl + "/api/v1/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your Fyn account";
        String body = "Welcome " + newProfile.getFullName() + ",\nPlease activate your Fyn account using the following link:\n" + activationLink + "\n" + "\nPlease do not respond to this email as it is auto-generated.";
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        // saved entity ko wapas DTO me convert karke return kar rahe hain
        return toDTO(newProfile);
    }


    // DTO to Entity conversion ka helper
    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImage(profileDTO.getProfileImage())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    // Entity to DTO conversion ka helper
    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImage(profileEntity.getProfileImage())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    //    validating the token
    public boolean activateProfile(String activationToken) {
//        checking the validation of token from the database
        return profileRepository.findByActivationToken(activationToken).map(profile -> {
//            setting the profile(user) is active
            profile.setIsActive(true);
//            saving the profile back to db
            profileRepository.save(profile);
            return true;
        }).orElse(false);
    }

    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email).map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Profile not found with this email:" + authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Profile not found with this Email"));
        }
//        conveting the profile entity to DTo
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .email(currentUser.getEmail())
                .fullName(currentUser.getFullName())
                .profileImage(currentUser.getProfileImage())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));

            String token = jwtutil.generateToken(authDTO.getEmail());
            return Map.of("token", token, "user", getPublicProfile(authDTO.getEmail()));
        } catch (Exception e) {
            throw new RuntimeException("Invald Email or Password");
        }
    }
}