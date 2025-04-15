package com.appxemphim.firebaseBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appxemphim.firebaseBackend.security.JwtUtil;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import com.google.firebase.cloud.FirestoreClient;

@Service
public class AccountService {
    private final Firestore db = FirestoreClient.getFirestore();
    @Autowired
    private JwtUtil jwtUtil;



    public String createToken(String uid ) {
        try{        
            DocumentReference roleRef = db.collection("Account_role").document(uid);
            DocumentSnapshot snapshot = roleRef.get().get();
            if (!snapshot.exists()) {
                throw new RuntimeException("User with UID " + uid + " not found in Account_role.");
            }
            Object roleObj = snapshot.get("role");
            String roleId = String.valueOf(roleObj);
            if (roleId == null) {
                throw new RuntimeException("Role ID is invalid.");
            }
            DocumentSnapshot roleSnapshot = db.collection("Role").document(roleId).get().get();
            if (!roleSnapshot.exists()) {
                throw new RuntimeException("Role " + roleId + " not found in Role collection.");
            }
            String role = roleSnapshot.getString("name");
            return jwtUtil.createJwtToken(uid, role);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
}
