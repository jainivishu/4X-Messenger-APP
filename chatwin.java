package com.example.fourxmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class chatwin extends AppCompatActivity {
    String reciverimg,reciverUid,reciverName,SenderUID;
    CircleImageView profile;
    TextView reciverNName;
    CardView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    public static String senderImg;
    public static String reciverIImg;

    String senderRoom,reciverRoom;
    RecyclerView mmessangesAdpter;
    ArrayList<msgModelclass>messagessArrayList;
    messagesAdpter messsgesAdpter1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);
        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mmessangesAdpter=findViewById(R.id.msgadpter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessangesAdpter.setLayoutManager(linearLayoutManager);
        messsgesAdpter1 =new messagesAdpter(chatwin.this,messagessArrayList);
        mmessangesAdpter.setAdapter(messsgesAdpter1);



        reciverName=getIntent().getStringExtra("nameee");
        reciverimg=getIntent().getStringExtra("reciverImg");
        reciverUid=getIntent().getStringExtra("uid");
        sendbtn=findViewById(R.id.sendbtnn);
        textmsg=findViewById(R.id.txtmsg);
        messagessArrayList=new ArrayList<>();


        profile=findViewById(R.id.profileimgg);

        reciverNName=findViewById(R.id.recivername);
        Picasso.get().load(reciverimg).into(profile);
        reciverNName.setText(""+reciverName);
        SenderUID=firebaseAuth.getUid();
        senderRoom=SenderUID+reciverUid;
        reciverRoom=reciverUid+SenderUID;

        DatabaseReference reference=database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatreference=database.getReference().child("user").child(senderRoom).child("messages");
        chatreference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagessArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass messages=dataSnapshot.getValue(msgModelclass.class);
                    messagessArrayList.add(messages);

                }
                messsgesAdpter1.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg=snapshot.child("profilepic").getValue().toString();
                reciverIImg=reciverimg;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });





        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=textmsg.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(chatwin.this, "Enter the message!!", Toast.LENGTH_SHORT).show();
                    return;

                }
                textmsg.setText("");
                Date date=new Date();
                msgModelclass messagess2=new msgModelclass(message,SenderUID,date.getTime());
                database=FirebaseDatabase.getInstance();
                database.getReference().child("chats").child("senderRoom").child("messages").push().setValue(messagess2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats").child("reciverRoom").child("messages").push().setValue(messagess2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                    }
                });
            }
        });


    }
}