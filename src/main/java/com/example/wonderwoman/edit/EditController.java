package com.example.wonderwoman.edit;
import com.example.wonderwoman.login.CurrentUser;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/edit")
public class EditController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EditController(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/change-nickname")
    public String changeNickname(@PathVariable Long userId, @RequestParam String newNickname) {
        Optional<Member> optionalMember = memberRepository.findById(userId);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.updateNickname(newNickname);
            memberRepository.save(member);
            return "Nickname changed successfully.";
        } else {
            return "Member not found.";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@PathVariable Long userId, @RequestParam String currentPassword, @RequestParam String newPassword) {
        Optional<Member> optionalMember = memberRepository.findById(userId);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (passwordEncoder.matches(currentPassword, member.getPassword())) {
                String newPasswordEncoded = passwordEncoder.encode(newPassword);
                member.updatePassword(newPasswordEncoded);
                memberRepository.save(member);
                return "Password changed successfully.";
            } else {
                return "Current password is incorrect.";
            }
        } else {
            return "Member not found.";
        }
    }

    @PostMapping("/change-image-url")
    public String changeImageUrl(@PathVariable Long userId, @RequestParam String newImageUrl) {
        Optional<Member> optionalMember = memberRepository.findById(userId);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.updateImage(newImageUrl);
            memberRepository.save(member);
            return "Image URL changed successfully.";
        } else {
            return "Member not found.";
        }
    }

}
