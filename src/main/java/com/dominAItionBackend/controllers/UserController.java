package com.dominAItionBackend.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dominAItionBackend.models.EmailVerificationToken;
import com.dominAItionBackend.models.Game;
import com.dominAItionBackend.models.PasswordResetToken;
import com.dominAItionBackend.models.User;
import com.dominAItionBackend.models.World;
import com.dominAItionBackend.repository.EmailVerificationTokenRepository;
import com.dominAItionBackend.repository.GameRepository;
import com.dominAItionBackend.repository.PasswordResetTokenRepository;
import com.dominAItionBackend.repository.UserRepository;
import com.dominAItionBackend.repository.WorldRepository;
import com.dominAItionBackend.service.EmailService;



@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://dominaition-frontend.onrender.com"
})
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private WorldRepository worldRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // optional: check duplicates
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
        broadcastLoginToFriends(user);
        return ResponseEntity.ok(user);
    }

    private void broadcastLoginToFriends(User user) {
        if (user.getFriendIds() == null) return;

        for (String friendId : user.getFriendIds()) {
            userRepository.findById(friendId).ifPresent(friend -> {
                boolean notificationsEnabled = friend.isNotificationsEnabled(); // get flag

                // Send message to each friend's WebSocket channel
                simpMessagingTemplate.convertAndSend(
                    "/topic/user/" + friend.getId() + "/notifications",
                    Map.of(
                        "type", "FRIEND_ONLINE",
                        "userId", user.getId(),
                        "username", user.getUsername(),
                        "notificationsEnabled", notificationsEnabled
                    )
                );
            });
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updated) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setUsername(updated.getUsername());
                    existing.setBio(updated.getBio());
                    existing.setIcon(updated.getIcon());
                    existing.setPublic(updated.isPublic());
                    return ResponseEntity.ok(userRepository.save(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/addFriend/{email}/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable String email, @PathVariable String friendId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Initialize friend list if null
        if (user.getFriendIds() == null) {
            user.setFriendIds(new ArrayList<>());
        }

        // Add friend only if not already present
        if (!user.getFriendIds().contains(friendId)) {
            user.getFriendIds().add(friendId);
            userRepository.save(user);
        }

        return ResponseEntity.ok(user);
    }

    // Send a friend request
    @PutMapping("/sendFriendRequest/{fromEmail}/{toEmail}")
    public ResponseEntity<?> sendFriendRequest(
            @PathVariable String fromEmail,
            @PathVariable String toEmail) {

        User fromUser = userRepository.findByEmail(fromEmail);
        User toUser = userRepository.findByEmail(toEmail);

        if (fromUser == null || toUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        if (toUser.getFriendRequestIds() == null) {
            toUser.setFriendRequestIds(new ArrayList<>());
        }

        // Avoid duplicates or sending to an existing friend
        if (!toUser.getFriendRequestIds().contains(fromUser.getId())
                && !toUser.getFriendIds().contains(fromUser.getId())) {

            toUser.getFriendRequestIds().add(fromUser.getId());
            userRepository.save(toUser);
        }

        if (toUser.getBlockedIds() != null && toUser.getBlockedIds().contains(fromUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You cannot send a request to this user.");
        }

        return ResponseEntity.ok("Friend request sent!");
    }
    // Approve a friend request
    @PutMapping("/approveFriendRequest/{toEmail}/{fromId}")
    public ResponseEntity<?> approveFriendRequest(
            @PathVariable String toEmail,
            @PathVariable String fromId) {

        User toUser = userRepository.findByEmail(toEmail);
        User fromUser = userRepository.findById(fromId).orElse(null);

        if (toUser == null || fromUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        // Remove from pending list
        if (toUser.getFriendRequestIds() != null) {
            toUser.getFriendRequestIds().remove(fromId);
        }

        // Initialize friend lists
        if (toUser.getFriendIds() == null) toUser.setFriendIds(new ArrayList<>());
        if (fromUser.getFriendIds() == null) fromUser.setFriendIds(new ArrayList<>());

        // Add each other
        if (!toUser.getFriendIds().contains(fromId)) toUser.getFriendIds().add(fromId);
        if (!fromUser.getFriendIds().contains(toUser.getId())) fromUser.getFriendIds().add(toUser.getId());

        userRepository.save(toUser);
        userRepository.save(fromUser);

        return ResponseEntity.ok("Friend request approved!");
    }
    // Reject a friend request
    @PutMapping("/rejectFriendRequest/{toEmail}/{fromId}")
    public ResponseEntity<?> rejectFriendRequest(
            @PathVariable String toEmail,
            @PathVariable String fromId) {

        User toUser = userRepository.findByEmail(toEmail);
        if (toUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        if (toUser.getFriendRequestIds() != null) {
            toUser.getFriendRequestIds().remove(fromId);
            userRepository.save(toUser);
        }

        return ResponseEntity.ok("Friend request rejected!");
    }
    // Remove a friend from friends list
    @PutMapping("/removeFriend/{email}/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable String email, @PathVariable String friendId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Remove friendId from user's list
        if (user.getFriendIds() != null && user.getFriendIds().contains(friendId)) {
            user.getFriendIds().remove(friendId);
            userRepository.save(user);
        }

        // Remove user from friend's list (reverse removal)
        Optional<User> friendOpt = userRepository.findById(friendId);
        if (friendOpt.isPresent()) {
            User friend = friendOpt.get();
            if (friend.getFriendIds() != null && friend.getFriendIds().contains(user.getId())) {
                friend.getFriendIds().remove(user.getId());
                userRepository.save(friend);
            }
        }

        return ResponseEntity.ok(user);
    }
    // Cancel a friend request
    @PutMapping("/cancelFriendRequest/{fromEmail}/{toEmail}")
    public ResponseEntity<?> cancelFriendRequest(
            @PathVariable String fromEmail,
            @PathVariable String toEmail) {

        User fromUser = userRepository.findByEmail(fromEmail);
        User toUser = userRepository.findByEmail(toEmail);

        if (fromUser == null || toUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        // If the receiver has a pending request from the sender, remove it
        if (toUser.getFriendRequestIds() != null &&
            toUser.getFriendRequestIds().contains(fromUser.getId())) {

            toUser.getFriendRequestIds().remove(fromUser.getId());
            userRepository.save(toUser);
        }

        return ResponseEntity.ok("Friend request canceled successfully!");
    }
    @GetMapping("/friends/{email}")
    public ResponseEntity<?> getFriendsByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        // If they have no friends, return an empty list
        if (user.getFriendIds() == null || user.getFriendIds().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // Find each friend by ID (filter out missing ones)
        List<Map<String, String>> friendList = user.getFriendIds().stream()
                .map(friendId -> userRepository.findById(friendId).orElse(null))
                .filter(f -> f != null)
                .map(f -> Map.of(
                        "id", f.getId(),
                        "email", f.getEmail(),
                        "name", f.getUsername()))
                .toList();

        return ResponseEntity.ok(friendList);
    }

    // Block a user (remove from friends and prevent future requests)
    @PutMapping("/blockUser/{email}/{blockedId}")
    public ResponseEntity<?> blockUser(
            @PathVariable String email,
            @PathVariable String blockedId) {

        User user = userRepository.findByEmail(email);
        User blockedUser = userRepository.findById(blockedId).orElse(null);

        if (user == null || blockedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        // Initialize blocked list if null
        if (user.getBlockedIds() == null) user.setBlockedIds(new ArrayList<>());

        // Remove from friends if currently friends
        if (user.getFriendIds() != null) user.getFriendIds().remove(blockedId);
        if (blockedUser.getFriendIds() != null) blockedUser.getFriendIds().remove(user.getId());

        // Remove any pending friend requests in either direction
        if (user.getFriendRequestIds() != null) user.getFriendRequestIds().remove(blockedId);
        if (blockedUser.getFriendRequestIds() != null) blockedUser.getFriendRequestIds().remove(user.getId());

        // Add to block list if not already there
        if (!user.getBlockedIds().contains(blockedId)) {
            user.getBlockedIds().add(blockedId);
        }

        // Save both users
        userRepository.save(user);
        userRepository.save(blockedUser);

        return ResponseEntity.ok("User blocked successfully.");
    }
    // Unblock a user
    @PutMapping("/unblockUser/{email}/{unblockedId}")
    public ResponseEntity<?> unblockUser(
            @PathVariable String email,
            @PathVariable String unblockedId) {

        User user = userRepository.findByEmail(email);
        User unblockedUser = userRepository.findById(unblockedId).orElse(null);

        if (user == null || unblockedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        if (user.getBlockedIds() != null) {
            user.getBlockedIds().remove(unblockedId);
        }

        userRepository.save(user);
        return ResponseEntity.ok("User unblocked successfully.");
    }
    
    @PutMapping("/updateProfile/{email}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String email,
            @RequestBody Map<String, String> body) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String newUsername = body.get("username");
        String newBio = body.get("bio");
        boolean newNotificationsEnabled = body.get("notificationsEnabled").equalsIgnoreCase("true");

        if (newUsername == null || newUsername.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be empty");
        }

        // Prevent duplicate usernames
        List<User> allUsers = userRepository.findAll();
        boolean duplicate = allUsers.stream()
            .anyMatch(u -> u.getUsername().equalsIgnoreCase(newUsername) && !u.getEmail().equals(email));

        if (duplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        user.setUsername(newUsername);
        user.setBio(newBio);
        user.setNotificationsEnabled(newNotificationsEnabled);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/verify/{email}")
    public ResponseEntity<String> sendVerificationEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (user.isEmailVerified()) {
            return ResponseEntity.ok("Already verified");
        }

        String token = UUID.randomUUID().toString();
        Date expiry = new Date(System.currentTimeMillis() + 15 * 60 * 1000); // 15 mins

        EmailVerificationToken evToken = new EmailVerificationToken(user.getId(), token, expiry);
        tokenRepository.save(evToken);

        String link = "http://localhost:8080/api/users/verify/confirm?token=" + token;

        try {
            emailService.sendVerificationEmail(user.getEmail(), link);
            return ResponseEntity.ok("Verification email sent to " + user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending email: " + e.getMessage());
        }
    }
    @GetMapping("/verify/confirm")
    public ResponseEntity<String> confirmVerification(@RequestParam("token") String token) {
        Optional<EmailVerificationToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }

        EmailVerificationToken evToken = tokenOpt.get();
        User user = userRepository.findById(evToken.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        user.setEmailVerified(true);
        userRepository.save(user);
        tokenRepository.delete(evToken);

        return ResponseEntity.ok("Email verified successfully!");
    }

    @PutMapping("/forgotPassword/{email}")
    public ResponseEntity<?> sendPasswordResetEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Create token that expires in 15 minutes
        String token = UUID.randomUUID().toString();
        Date expiry = new Date(System.currentTimeMillis() + 15 * 60 * 1000);

        PasswordResetToken resetToken = new PasswordResetToken(user.getId(), token, expiry);
        passwordResetTokenRepository.save(resetToken);

        String link = "http://localhost:3000/reset?token=" + token;

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), link);
            return ResponseEntity.ok("Password reset email sent to " + user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send reset email: " + e.getMessage());
        }
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");

        var tokenOpt = passwordResetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }

        PasswordResetToken resetToken = tokenOpt.get();
        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expired");
        }

        User user = userRepository.findById(resetToken.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        System.out.println("⚙️ Resetting password for user: " + user.getEmail());
        System.out.println("🆕 New raw password (debug): " + newPassword);

        user.setPassword(newPassword);
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);

        emailService.sendPasswordChangeConfirmation(user.getEmail());

        return ResponseEntity.ok("Password successfully reset.");
    }

    @PutMapping("/updatePassword/{email}")
    public ResponseEntity<User> updatePassword(@PathVariable String email, @RequestBody String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        System.out.println("✅ New password stored in MongoDB for " + email);

        return ResponseEntity.ok(user);
    }

    /* ---------------------- BACKGROUND MUSIC ---------------------- */
    @PutMapping("/backgroundMusic/{email}")
    public ResponseEntity<Map<String, Object>> updateMusicPreference(
            @PathVariable String email,
            @RequestBody Map<String, Boolean> body) {

        boolean musicEnabled = body.getOrDefault("musicEnabled", true);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "User not found"));
        }
        
        user.setMusicEnabled(musicEnabled);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Background music preference updated successfully",
                "musicEnabled", musicEnabled
        ));
    }

    //fetching games
    @GetMapping("/fetchGames/{userId}")
    public ResponseEntity<Map<String, Object>> fetchGames(
            @PathVariable String userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "User not found"));
        }

        List<String> savedGames = user.getSavedGameIds();
        System.out.println("Saved games for user " + userId + ": " + savedGames);

        // Retrieve all games for those IDs
        List<Map<String, Object>> gameInfoList = new ArrayList<>();
        for (String gameId : savedGames) {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game != null) {
                Map<String, Object> gameInfo = new HashMap<>();
                World world = worldRepository.findById(game.getWorldId()).orElse(null);
                String leadingPlayerName = "N/A";
                Map<String, Integer> playerPoints = game.getPlayerPoints();
                if (playerPoints != null && !playerPoints.isEmpty()) {
                    String leadingPlayerId = Collections.max(playerPoints.entrySet(),
                            Map.Entry.comparingByValue()).getKey();
                    leadingPlayerName = leadingPlayerId; // Default to ID
                    // Try to get username
                    User leadingPlayer = userRepository.findById(leadingPlayerId).orElse(null);
                    if (leadingPlayer != null) {
                        leadingPlayerName = leadingPlayer.getUsername();
                    }
                }

                String summary = game.getSummary();
                if (summary == null || summary.isEmpty()) {
                    summary = "No summary available.";
                }

                gameInfo.put("gameId", game.getId());
                gameInfo.put("World Name", world.getWorldName());
                gameInfo.put("pointsToWin", game.getWinningPoints());
                gameInfo.put("status", game.getStatus());
                gameInfo.put("leadingPlayerName", leadingPlayerName);
                gameInfo.put("summary", summary);

                //System.out.println("Fetched game: " + gameInfo);
                gameInfoList.add(gameInfo);
            }
        }

        // Return as JSON
        return ResponseEntity.ok(Map.of(
                "success", true,
                "games", gameInfoList
        ));
    }

    //fetching games with requester ID
    @GetMapping("/fetchGames/{userId}/{requesterId}")
    public ResponseEntity<Map<String, Object>> fetchGames(
            @PathVariable String userId, @PathVariable String requesterId) {

        User user = userRepository.findById(userId).orElse(null);
        List<String> blockedIds = user.getBlockedIds();
        if (blockedIds != null && blockedIds.contains(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "You are blocked by this user."));
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        }

        List<String> savedGames = user.getSavedGameIds();
        System.out.println("Saved games for user " + userId + ": " + savedGames);

        // Retrieve all games for those IDs
        List<Map<String, Object>> gameInfoList = new ArrayList<>();
        for (String gameId : savedGames) {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game != null) {
                Map<String, Object> gameInfo = new HashMap<>();
                World world = worldRepository.findById(game.getWorldId()).orElse(null);
                String leadingPlayerName = "N/A";
                Map<String, Integer> playerPoints = game.getPlayerPoints();
                if (playerPoints != null && !playerPoints.isEmpty()) {
                    String leadingPlayerId = Collections.max(playerPoints.entrySet(),
                            Map.Entry.comparingByValue()).getKey();
                    leadingPlayerName = leadingPlayerId; // Default to ID
                    // Try to get username
                    User leadingPlayer = userRepository.findById(leadingPlayerId).orElse(null);
                    if (leadingPlayer != null) {
                        leadingPlayerName = leadingPlayer.getUsername();
                    }
                }

                String summary = game.getSummary();
                if (summary == null || summary.isEmpty()) {
                    summary = "No summary available.";
                }

                gameInfo.put("gameId", game.getId());
                gameInfo.put("World Name", world.getWorldName());
                gameInfo.put("pointsToWin", game.getWinningPoints());
                gameInfo.put("status", game.getStatus());
                gameInfo.put("leadingPlayerName", leadingPlayerName);
                gameInfo.put("summary", summary);

                //System.out.println("Fetched game: " + gameInfo);
                gameInfoList.add(gameInfo);
            }
        }

        // Return as JSON
        return ResponseEntity.ok(Map.of(
                "success", true,
                "games", gameInfoList
        ));
    }

    /* WINS AND LOSSES */
    @PutMapping("/wins/{email}")
    public int getWins(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return -1;
        }

        int wins = user.getWins();
        user.setWins(user.getWins() + 1);
        userRepository.save(user);

        return wins + 1;
    }

    /* GAMES PLAYED */
    @PutMapping("/gamesPlayed/{email}")
    public ResponseEntity<?> incrementGamesPlayed(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        }

        int newGamesPlayed = user.getGamesPlayed() + 1;
        user.setGamesPlayed(newGamesPlayed);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Games played incremented successfully",
                "gamesPlayed", newGamesPlayed
        ));
    }

 
    /* TOTAL PLAY TIME */
    @PutMapping("/totalPlayTime/{email}")
    public ResponseEntity<?> incrementTotalPlayTime(@PathVariable String email, @RequestBody Map<String, Double> body) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        }

        double hoursPlayed = body.getOrDefault("hours", 0.0);
        double newTotal = user.getTotalPlayTime() + hoursPlayed;

        user.setTotalPlayTime(newTotal);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Total play time incremented successfully",
                "totalPlayTime", newTotal
        ));
    }

    /* CONVERT GUEST ACCOUNT INTO REAL ACCOUNT */
    @PostMapping("/convertGuest/{guestEmail}")
    public ResponseEntity<?> convertGuest(
            @PathVariable String guestEmail,
            @RequestBody Map<String, String> body) {

        User guest = userRepository.findByEmail(guestEmail);
        if (guest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Guest account not found");
        }

        String newUsername = body.get("newGuestUsername");
        String newEmail = body.get("newGuestEmail");
        String newPassword = body.get("newGuestPassword");

        if (newUsername == null || newUsername.trim().isEmpty() ||
            newEmail == null || newEmail.trim().isEmpty() ||
            newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username, email, and password are required");
        }

        // Check if new username or email already exists
        if (userRepository.findByUsername(newUsername) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }
        if (userRepository.findByEmail(newEmail) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already taken");
        }

        // Create new user preserving stats
        User newUser = new User();
        newUser.setUsername(newUsername);
        newUser.setEmail(newEmail);
        newUser.setPassword(newPassword); // Ideally hashed using passwordEncoder
        newUser.setWins(guest.getWins());
        newUser.setLosses(guest.getLosses());
        newUser.setGamesPlayed(guest.getGamesPlayed());
        newUser.setTotalPlayTime(guest.getTotalPlayTime());
        newUser.setFriendIds(new ArrayList<>(guest.getFriendIds()));
        newUser.setFriendRequestIds(new ArrayList<>(guest.getFriendRequestIds()));
        newUser.setSavedGameIds(new ArrayList<>(guest.getSavedGameIds()));
        newUser.setSavedWorldIds(new ArrayList<>(guest.getSavedWorldIds()));
        newUser.setSavedCharIds(new ArrayList<>(guest.getSavedCharIds()));
        newUser.setAchievementIds(new ArrayList<>(guest.getAchievementIds()));
        newUser.setNotificationsEnabled(guest.isNotificationsEnabled());
        newUser.setMusicEnabled(guest.isMusicEnabled());
        newUser.setBio(guest.getBio());
        newUser.setIcon(guest.getIcon());
        newUser.setPublic(guest.isPublic());
        newUser.setOnline(guest.isOnline());
        newUser.setHuman(true);
        newUser.setEmailVerified(false); // Need to verify new email

        // Save new user
        User savedUser = userRepository.save(newUser);

        // Optionally, you can mark guest account as inactive or delete it
        // guest.setEmail(guest.getEmail() + "_old");
        // guest.setUsername(guest.getUsername() + "_old");
        // userRepository.save(guest);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Guest account converted successfully",
            "user", savedUser
        ));
    }

    @PostMapping("/history")
    public List<Game> getHistory(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.get("userId");
    
        // Filter completed games where the user participated
        return gameRepository.findAll().stream()
                .filter(game -> "done".equals(game.getStatus()) && game.getPlayerIds().contains(userId))
                .toList(); // Returns a List<Game>
    }

    @PostMapping("/savedGames")
    public List<Game> savedGames(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.get("userId");
    
        // Filter completed games where the user participated
        return gameRepository.findAll().stream()
                .filter(game -> !"done".equals(game.getStatus()) && game.getPlayerIds().contains(userId))
                .toList(); // Returns a List<Game>
    }
    
    



}
