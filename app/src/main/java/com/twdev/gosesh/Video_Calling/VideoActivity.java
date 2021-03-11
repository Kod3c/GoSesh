package com.twdev.gosesh.Video_Calling;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.twdev.gosesh.CodeClasses.ApiRequest;
import com.twdev.gosesh.CodeClasses.Callback;
import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.R;
import com.twdev.gosesh.Video_Calling.util.CameraCapturerCompat;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.twilio.video.AudioCodec;
import com.twilio.video.CameraCapturer;
import com.twilio.video.CameraCapturer.CameraSource;
import com.twilio.video.ConnectOptions;
import com.twilio.video.EncodingParameters;
import com.twilio.video.G722Codec;
import com.twilio.video.H264Codec;
import com.twilio.video.IsacCodec;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.OpusCodec;
import com.twilio.video.PcmaCodec;
import com.twilio.video.PcmuCodec;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoCodec;
import com.twilio.video.VideoRenderer;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;
import com.twilio.video.Vp8Codec;
import com.twilio.video.Vp9Codec;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

public class VideoActivity extends AppCompatActivity {
    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "VideoActivity";

    public static boolean is_calling_activity_open=false;

    public static final String Call_Receive="Call_Receive";
    public static final String Call_Send="Call_Send";
    public static final String Call_Ringing="Call_Ringing";
    public static final String Call_Pick="Call_Pick";
    public static final String Call_not_Pick="Call_not_Pick";


    private static final String LOCAL_AUDIO_TRACK_NAME = "mic";
    private static final String LOCAL_VIDEO_TRACK_NAME = "camera";



    public static String identity;

    private String accessToken;

    private Room room;
    private LocalParticipant localParticipant;

    private AudioCodec audioCodec;
    private VideoCodec videoCodec;


    private EncodingParameters encodingParameters;


    private VideoView primaryVideoView;
    private VideoView thumbnailVideoView;


    private SharedPreferences default_preferences;
    private SharedPreferences sharedPreferences;

    private TextView videoStatusTextView;
    private CameraCapturerCompat cameraCapturerCompat;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private FloatingActionButton connectActionFab,switchCameraActionFab,localVideoActionFab,muteActionFab,speaker_action_fab;
    private ProgressBar reconnectingProgressBar;
    private AudioManager audioManager;
    private String remoteParticipantIdentity;

    private int previousAudioMode;
    private boolean previousMicrophoneMute;
    private VideoRenderer localVideoView;
    private boolean disconnectedFromOnDestroy;
    private boolean isSpeakerPhoneEnabled = true;
    private boolean enableAutomaticSubscription;



    String caller_id,caller_name,caller_image,call_status,call_type,roomname;

    public DatabaseReference rootref;

    Ringtone ringtoneSound;

    LinearLayout calling_action_btns;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_video);

        sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

        identity= sharedPreferences.getString(Variables.uid,"0");


        rootref= FirebaseDatabase.getInstance().getReference();

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtoneSound = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);



        primaryVideoView = findViewById(R.id.primary_video_view);
        thumbnailVideoView = findViewById(R.id.thumbnail_video_view);
        videoStatusTextView = findViewById(R.id.video_status_textview);
        reconnectingProgressBar = findViewById(R.id.reconnecting_progress_bar);


        calling_action_btns=findViewById(R.id.calling_action_btns);
        connectActionFab = findViewById(R.id.connect_action_fab);
        switchCameraActionFab = findViewById(R.id.switch_camera_action_fab);
        localVideoActionFab = findViewById(R.id.local_video_action_fab);
        muteActionFab = findViewById(R.id.mute_action_fab);
        speaker_action_fab=findViewById(R.id.speaker_action_fab);

        default_preferences = PreferenceManager.getDefaultSharedPreferences(this);


        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);


        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(isSpeakerPhoneEnabled);

        Intent intent=getIntent();

        onNewIntent(intent);


        createAudioAndVideoTracks();
        retrieveAccessTokenfromServer();

        intializeUI();

        get_user_token();




    }


    String caller_token="null";
    public void get_user_token(){


        rootref.child("Users")
                .child(caller_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.hasChild("token")){
                    caller_token=dataSnapshot.child("token").getValue().toString();
                }


                Show_Calling_Dialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    CountDownTimer countDownTimer;
    public void Start_Countdown_timer(){
        countDownTimer=new CountDownTimer(60000,2000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Send_notification(Call_not_Pick,"Calling you...");
                disconnectClickListener();
                finish();
            }
        };

        countDownTimer.start();
    }

    public void Stop_timer(){
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent!=null) {
            caller_id=intent.getStringExtra("id");
            caller_name=intent.getStringExtra("name");
            caller_image=intent.getStringExtra("image");
            call_status=intent.getStringExtra("status");
            call_type=intent.getStringExtra("call_type");
            roomname=intent.getStringExtra("roomname");
        }
        do_action_on_status();
    }



    TextView calling_status_txt;
    RelativeLayout calling_user_info_layout;
    public void Show_Calling_Dialog() {
        calling_user_info_layout=findViewById(R.id.calling_user_info_layout);

        calling_user_info_layout.setVisibility(View.VISIBLE);
        calling_action_btns.setVisibility(View.GONE);


        SimpleDraweeView userimage=findViewById(R.id.userimage);
        TextView username=findViewById(R.id.username);
        calling_status_txt=findViewById(R.id.calling_status_txt);

        if(caller_image!=null && !caller_image.equals("")) {
            Uri uri;
            if(caller_image.contains("http"))
                uri = Uri.parse(caller_image);
            else
                uri = Uri.parse(Variables.image_base_url+caller_image);

            userimage.setImageURI(uri);
        }else {
            Uri uri = Uri.parse("null");
            userimage.setImageURI(uri);
        }

        username.setText(caller_name);


        RelativeLayout receive_disconnect_layout=findViewById(R.id.receive_disconnect_layout);
        ImageButton pick_incoming_call=findViewById(R.id.pick_incoming_call);
        ImageButton cancel_incoming_btn=findViewById(R.id.cancel_incoming_btn);

        pick_incoming_call.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                if(accessToken!=null){

                    if(call_type.equals("video_call"))
                    calling_user_info_layout.setVisibility(View.GONE);
                    else {

                        switchCameraActionFab.setVisibility(View.GONE);
                        localVideoActionFab.setVisibility(View.GONE);
                        findViewById(R.id.receive_disconnect_layout).setVisibility(View.GONE);
                        findViewById(R.id.cancel_call).setVisibility(View.GONE);
                    }

                    calling_action_btns.setVisibility(View.VISIBLE);

                    if (ringtoneSound != null) {
                        ringtoneSound.stop();
                    }

                    VideoActivity.this.connectToRoom(roomname);
                    Send_notification(Call_Pick,"Pick your Call");
                } else {
                    Toast.makeText(VideoActivity.this, "Call Access token not Found", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancel_incoming_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send_notification(Call_not_Pick,"Hang out your call");
                if (ringtoneSound != null) {
                    ringtoneSound.stop();
                }
                finish();
            }
        });


        ImageButton cancel_call=findViewById(R.id.cancel_call);
        cancel_call.setVisibility(View.VISIBLE);
        cancel_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send_notification(Call_not_Pick,"Hang out your call");
                disconnectClickListener();
                finish();
            }
        });

        if(call_status.equals(Call_Send)){

            receive_disconnect_layout.setVisibility(View.GONE);
            cancel_call.setVisibility(View.VISIBLE);
            Send_notification(Call_Receive,"Calling you...");

            Start_Countdown_timer();

        }

        else if(call_status.equals(Call_Receive)){
            receive_disconnect_layout.setVisibility(View.VISIBLE);
            cancel_call.setVisibility(View.GONE);
            calling_status_txt.setText("Calling you");
            Send_notification(Call_Ringing,"Ringing...");

        }



    }



    @SuppressLint("RestrictedApi")
    public void do_action_on_status(){
        if(call_status.equals(Call_Pick)) {
            if (accessToken != null && calling_user_info_layout != null) {
                Stop_timer();

                if(call_type.equals("video_call"))
                   calling_user_info_layout.setVisibility(View.GONE);
                else {
                    switchCameraActionFab.setVisibility(View.GONE);
                    localVideoActionFab.setVisibility(View.GONE);
                    findViewById(R.id.receive_disconnect_layout).setVisibility(View.GONE);
                    findViewById(R.id.cancel_call).setVisibility(View.GONE);
                }

                calling_action_btns.setVisibility(View.VISIBLE);

                VideoActivity.this.connectToRoom(roomname);
                Toast.makeText(this, "Pick call", Toast.LENGTH_SHORT).show();
            }
        }

        else if(call_status.equals(Call_Receive)){

            if (ringtoneSound != null) {
                ringtoneSound.play();
            }
        }

        else if(call_status.equals(Call_Ringing)){
            if(calling_status_txt!=null)
                calling_status_txt.setText(R.string.ringing);
        }

        else if(call_status.equals(Call_not_Pick)){
            if (ringtoneSound != null) {
                ringtoneSound.stop();
            }
            Toast.makeText(this, caller_name+" hang out the call", Toast.LENGTH_SHORT).show();
            finish();
        }


    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            boolean cameraAndMicPermissionGranted = true;

            for (int grantResult : grantResults) {
                cameraAndMicPermissionGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
            }

            if (cameraAndMicPermissionGranted) {
                createAudioAndVideoTracks();
                retrieveAccessTokenfromServer();
            } else {
                Toast.makeText(this,
                       "Need mic and speaker permission",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onStart() {
        is_calling_activity_open=true;

        audioCodec = getAudioCodecPreference(TwilioSettings.PREF_AUDIO_CODEC,
                TwilioSettings.PREF_AUDIO_CODEC_DEFAULT);
        videoCodec = getVideoCodecPreference(TwilioSettings.PREF_VIDEO_CODEC,
                TwilioSettings.PREF_VIDEO_CODEC_DEFAULT);
        enableAutomaticSubscription = getAutomaticSubscriptionPreference(TwilioSettings.PREF_ENABLE_AUTOMATIC_SUBSCRIPTION,
                TwilioSettings.PREF_ENABLE_AUTOMATIC_SUBSCRIPTION_DEFAULT);

        final EncodingParameters newEncodingParameters = getEncodingParameters();


        if (localVideoTrack == null && call_type.equals("video_call")) {

            if(cameraCapturerCompat==null)
                cameraCapturerCompat = new CameraCapturerCompat(this, getAvailableCameraSource());


            localVideoTrack = LocalVideoTrack.create(this,
                    true,
                    cameraCapturerCompat.getVideoCapturer(),
                    LOCAL_VIDEO_TRACK_NAME);

            localVideoTrack.addRenderer(localVideoView);


            if (localParticipant != null) {
                localParticipant.publishTrack(localVideoTrack);

                if (!newEncodingParameters.equals(encodingParameters)) {
                    localParticipant.setEncodingParameters(newEncodingParameters);
                }
            }
        }


        encodingParameters = newEncodingParameters;

        audioManager.setSpeakerphoneOn(isSpeakerPhoneEnabled);

        if (room != null) {
            reconnectingProgressBar.setVisibility((room.getState() != Room.State.RECONNECTING) ?
                    View.GONE :
                    View.VISIBLE);
            videoStatusTextView.setText(getString(R.string.connected_to) +" "+ caller_name);
        }

        super.onStart();

    }





    @Override
    protected void onStop() {


        if (localVideoTrack != null) {

            if (localParticipant != null) {
                localParticipant.unpublishTrack(localVideoTrack);
            }

            localVideoTrack.release();
            localVideoTrack = null;
        }
        super.onStop();

    }



    @Override
    protected void onDestroy() {

        is_calling_activity_open=false;

        configureAudio(false);

        if (ringtoneSound != null && ringtoneSound.isPlaying()) {
            ringtoneSound.stop();
        }

        if (room != null && room.getState() != Room.State.DISCONNECTED) {
            room.disconnect();
            disconnectedFromOnDestroy = true;
        }


        if (localAudioTrack != null) {
            localAudioTrack.release();
            localAudioTrack = null;
        }

        if (localVideoTrack != null) {
            localVideoTrack.release();
            localVideoTrack = null;
        }

        Stop_limit_timer();
        super.onDestroy();
    }


    private void createAudioAndVideoTracks() {
        try {

            localAudioTrack = LocalAudioTrack.create(this, true, LOCAL_AUDIO_TRACK_NAME);

            if(call_type.equals("video_call")){
            cameraCapturerCompat = new CameraCapturerCompat(this, getAvailableCameraSource());
            localVideoTrack = LocalVideoTrack.create(this,
                    true,
                    cameraCapturerCompat.getVideoCapturer(),
                    LOCAL_VIDEO_TRACK_NAME);

            primaryVideoView.setMirror(true);

            if(localVideoTrack!=null){
            localVideoTrack.addRenderer(primaryVideoView);
            localVideoView = primaryVideoView;
        }

        }

        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private CameraSource getAvailableCameraSource() {
        return (CameraCapturer.isSourceAvailable(CameraSource.FRONT_CAMERA)) ?
                (CameraSource.FRONT_CAMERA) :
                (CameraSource.BACK_CAMERA);
    }


    private void connectToRoom(String roomName) {
        configureAudio(true);
        ConnectOptions.Builder connectOptionsBuilder = new ConnectOptions.Builder(accessToken)
                .roomName(roomName);


        if (localAudioTrack != null) {
            connectOptionsBuilder
                    .audioTracks(Collections.singletonList(localAudioTrack));
        }


        if (localVideoTrack != null && call_type.equals("video_call")) {
            connectOptionsBuilder.videoTracks(Collections.singletonList(localVideoTrack));
        }


        connectOptionsBuilder.preferAudioCodecs(Collections.singletonList(audioCodec));

        if(call_type.equals("video_call"))
        connectOptionsBuilder.preferVideoCodecs(Collections.singletonList(videoCodec));


        connectOptionsBuilder.encodingParameters(encodingParameters);
        connectOptionsBuilder.enableAutomaticSubscription(enableAutomaticSubscription);

        room = Video.connect(this, connectOptionsBuilder.build(), roomListener());
        setDisconnectAction();
    }


    private void intializeUI() {
        connectActionFab.show();
        switchCameraActionFab.show();
        switchCameraActionFab.setOnClickListener(switchCameraClickListener());
        localVideoActionFab.show();
        localVideoActionFab.setOnClickListener(localVideoClickListener());
        muteActionFab.show();
        muteActionFab.setOnClickListener(muteClickListener());
        speaker_action_fab.show();
        speaker_action_fab.setOnClickListener(speakerClickListener());

    }


    private AudioCodec getAudioCodecPreference(String key, String defaultValue) {
        final String audioCodecName = default_preferences.getString(key, defaultValue);

        switch (audioCodecName) {
            case IsacCodec.NAME:
                return new IsacCodec();
            case OpusCodec.NAME:
                return new OpusCodec();
            case PcmaCodec.NAME:
                return new PcmaCodec();
            case PcmuCodec.NAME:
                return new PcmuCodec();
            case G722Codec.NAME:
                return new G722Codec();
            default:
                return new OpusCodec();
        }
    }

    private VideoCodec getVideoCodecPreference(String key, String defaultValue) {
        final String videoCodecName = default_preferences.getString(key, defaultValue);

        switch (videoCodecName) {
            case Vp8Codec.NAME:
                boolean simulcast = default_preferences.getBoolean(TwilioSettings.PREF_VP8_SIMULCAST,
                        TwilioSettings.PREF_VP8_SIMULCAST_DEFAULT);
                return new Vp8Codec(simulcast);
            case H264Codec.NAME:
                return new H264Codec();
            case Vp9Codec.NAME:
                return new Vp9Codec();
            default:
                return new Vp8Codec();
        }
    }

    private boolean getAutomaticSubscriptionPreference(String key, boolean defaultValue) {
        return default_preferences.getBoolean(key, defaultValue);
    }

    private EncodingParameters getEncodingParameters() {
        final int maxAudioBitrate = Integer.parseInt(
                default_preferences.getString(TwilioSettings.PREF_SENDER_MAX_AUDIO_BITRATE,
                        TwilioSettings.PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT));
        final int maxVideoBitrate = Integer.parseInt(
                default_preferences.getString(TwilioSettings.PREF_SENDER_MAX_VIDEO_BITRATE,
                        TwilioSettings.PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT));

        return new EncodingParameters(maxAudioBitrate, maxVideoBitrate);
    }


    private void setDisconnectAction() {
        connectActionFab.show();
        connectActionFab.setOnClickListener(disconnectClickListener());
    }




    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            Snackbar.make(connectActionFab,
                    "Multiple participants are not currently support in this UI",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        remoteParticipantIdentity = remoteParticipant.getIdentity();
        videoStatusTextView.setText(caller_name +" "+ getString(R.string.joined));

        /*
         * Add remote participant renderer
         */
        if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            /*
             * Only render video tracks that are subscribed to
             */
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                addRemoteParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
            }
        }

        /*
         * Start listening for participant events
         */
        remoteParticipant.setListener(remoteParticipantListener());
    }


    private void addRemoteParticipantVideo(VideoTrack videoTrack) {
        moveLocalVideoToThumbnailView();
        primaryVideoView.setMirror(false);
        videoTrack.addRenderer(primaryVideoView);
    }

    private void moveLocalVideoToThumbnailView() {
        if (thumbnailVideoView.getVisibility() == View.GONE) {
            thumbnailVideoView.setVisibility(View.VISIBLE);
            localVideoTrack.removeRenderer(primaryVideoView);
            localVideoTrack.addRenderer(thumbnailVideoView);
            localVideoView = thumbnailVideoView;
            thumbnailVideoView.setMirror(cameraCapturerCompat.getCameraSource() ==
                    CameraSource.FRONT_CAMERA);
        }
    }


    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {
        videoStatusTextView.setText(caller_name +" left.");
        if (!remoteParticipant.getIdentity().equals(remoteParticipantIdentity)) {
            return;
        }

        /*
         * Remove remote participant renderer
         */
        if (!remoteParticipant.getRemoteVideoTracks().isEmpty()) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            /*
             * Remove video only if subscribed to participant track
             */
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                removeParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
            }
        }
        moveLocalVideoToPrimaryView();

        if(remoteParticipant.getIdentity().contains(caller_id)){
            Toast.makeText(this, caller_name+" hang out the call", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void removeParticipantVideo(VideoTrack videoTrack) {
        videoTrack.removeRenderer(primaryVideoView);
    }

    private void moveLocalVideoToPrimaryView() {
        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            thumbnailVideoView.setVisibility(View.GONE);
            if (localVideoTrack != null) {
                localVideoTrack.removeRenderer(thumbnailVideoView);
                localVideoTrack.addRenderer(primaryVideoView);
            }
            localVideoView = primaryVideoView;
            primaryVideoView.setMirror(cameraCapturerCompat.getCameraSource() ==
                    CameraSource.FRONT_CAMERA);
        }
    }


    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                localParticipant = room.getLocalParticipant();
                videoStatusTextView.setText(getString(R.string.connected_to)+" "+ caller_name);
                calling_status_txt.setText(getString(R.string.connected));

                setTitle(room.getName());

                Start_limit_Timer();

                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
                    break;
                }
            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
                videoStatusTextView.setText(getString(R.string.reconnecting_to) +caller_name);
                calling_status_txt.setText(R.string.reconnecting);
                reconnectingProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReconnected(@NonNull Room room) {
                videoStatusTextView.setText(getString(R.string.connected_to) + room.getName());
                calling_status_txt.setText(R.string.connected);
                reconnectingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onConnectFailure(Room room, TwilioException e) {
                videoStatusTextView.setText(R.string.failed_to_connect);
                calling_status_txt.setText(R.string.disconnected);
                configureAudio(false);
                intializeUI();
            }

            @Override
            public void onDisconnected(Room room, TwilioException e) {
                localParticipant = null;
                videoStatusTextView.setText(getString(R.string.disconnected_from) +caller_name);
                reconnectingProgressBar.setVisibility(View.GONE);
                VideoActivity.this.room = null;
                // Only reinitialize the UI if disconnect was not called from onDestroy()
                if (!disconnectedFromOnDestroy) {
                    configureAudio(false);
                    intializeUI();
                    moveLocalVideoToPrimaryView();
                }

                Stop_limit_timer();
            }

            @Override
            public void onParticipantConnected(Room room, RemoteParticipant remoteParticipant) {
                addRemoteParticipant(remoteParticipant);

            }

            @Override
            public void onParticipantDisconnected(Room room, RemoteParticipant remoteParticipant) {
                removeRemoteParticipant(remoteParticipant);
            }

            @Override
            public void onRecordingStarted(Room room) {

                Log.d(TAG, "onRecordingStarted");
            }

            @Override
            public void onRecordingStopped(Room room) {

                Log.d(TAG, "onRecordingStopped");
            }
        };
    }

    private RemoteParticipant.Listener remoteParticipantListener() {
        return new RemoteParticipant.Listener() {
            @Override
            public void onAudioTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i(TAG, String.format("onAudioTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));
                videoStatusTextView.setText(R.string.audio_connected);
            }

            @Override
            public void onAudioTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i(TAG, String.format("onAudioTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));
                videoStatusTextView.setText(R.string.audio_unpublished);
            }

            @Override
            public void onDataTrackPublished(RemoteParticipant remoteParticipant,
                                             RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i(TAG, String.format("onDataTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));

            }

            @Override
            public void onDataTrackUnpublished(RemoteParticipant remoteParticipant,
                                               RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i(TAG, String.format("onDataTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));

            }

            @Override
            public void onVideoTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i(TAG, String.format("onVideoTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));
                videoStatusTextView.setText(R.string.video_connected);
            }

            @Override
            public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i(TAG, String.format("onVideoTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));
                videoStatusTextView.setText(R.string.video_unpublished);
            }

            @Override
            public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteAudioTrackPublication remoteAudioTrackPublication,
                                               RemoteAudioTrack remoteAudioTrack) {
                Log.i(TAG, String.format("onAudioTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));
                videoStatusTextView.setText(R.string.audio_subscribed);
            }

            @Override
            public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                 RemoteAudioTrack remoteAudioTrack) {
                Log.i(TAG, String.format("onAudioTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));
                videoStatusTextView.setText(R.string.audio_unsubscribed);
            }

            @Override
            public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                       TwilioException twilioException) {
                Log.i(TAG, String.format("onAudioTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
                videoStatusTextView.setText(R.string.audio_subscription_failed);
            }

            @Override
            public void onDataTrackSubscribed(RemoteParticipant remoteParticipant,
                                              RemoteDataTrackPublication remoteDataTrackPublication,
                                              RemoteDataTrack remoteDataTrack) {
                Log.i(TAG, String.format("onDataTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));

            }

            @Override
            public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                RemoteDataTrackPublication remoteDataTrackPublication,
                                                RemoteDataTrack remoteDataTrack) {
                Log.i(TAG, String.format("onDataTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));

            }

            @Override
            public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                      RemoteDataTrackPublication remoteDataTrackPublication,
                                                      TwilioException twilioException) {
                Log.i(TAG, String.format("onDataTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));

            }

            @Override
            public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               RemoteVideoTrack remoteVideoTrack) {
                Log.i(TAG, String.format("onVideoTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));
                videoStatusTextView.setText(R.string.video_subscribed);
                addRemoteParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                 RemoteVideoTrack remoteVideoTrack) {
                Log.i(TAG, String.format("onVideoTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));
                videoStatusTextView.setText(R.string.video_unsubscribed);
                removeParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                       TwilioException twilioException) {
                Log.i(TAG, String.format("onVideoTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
                videoStatusTextView.setText(R.string.video_subscription_failed);
                Snackbar.make(connectActionFab,
                        String.format("Failed to subscribe to %s video track",
                                remoteParticipant.getIdentity()),
                        Snackbar.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onAudioTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onVideoTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }
        };
    }



    private View.OnClickListener disconnectClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (room != null) {
                    room.disconnect();
                }
                VideoActivity.this.intializeUI();
                finish();
            }
        };
    }

    private View.OnClickListener switchCameraClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraCapturerCompat != null) {
                    CameraSource cameraSource = cameraCapturerCompat.getCameraSource();
                    cameraCapturerCompat.switchCamera();
                    if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
                        thumbnailVideoView.setMirror(cameraSource == CameraSource.BACK_CAMERA);
                    } else {
                        primaryVideoView.setMirror(cameraSource == CameraSource.BACK_CAMERA);
                    }
                }
            }
        };
    }

    private View.OnClickListener localVideoClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Enable/disable the local video track
                 */
                if (localVideoTrack != null) {
                    boolean enable = !localVideoTrack.isEnabled();
                    localVideoTrack.enable(enable);
                    int icon;
                    if (enable) {
                        icon = R.drawable.ic_videocam_white_24dp;
                        switchCameraActionFab.show();
                    } else {
                        icon = R.drawable.ic_videocam_off_black_24dp;
                        switchCameraActionFab.hide();
                    }
                    localVideoActionFab.setImageDrawable(
                            ContextCompat.getDrawable(VideoActivity.this, icon));
                }
            }
        };
    }

    private View.OnClickListener muteClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Enable/disable the local audio track. The results of this operation are
                 * signaled to other Participants in the same Room. When an audio track is
                 * disabled, the audio is muted.
                 */
                if (localAudioTrack != null) {
                    boolean enable = !localAudioTrack.isEnabled();
                    localAudioTrack.enable(enable);
                    int icon = enable ?
                            R.drawable.ic_mic_white_24dp : R.drawable.ic_mic_off_black_24dp;
                    muteActionFab.setImageDrawable(ContextCompat.getDrawable(
                            VideoActivity.this, icon));
                }
            }
        };
    }


    private View.OnClickListener speakerClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    speaker_action_fab.setImageResource(R.drawable.ic_volume_mute_black_24dp);
                    isSpeakerPhoneEnabled = false;
                } else {
                    audioManager.setSpeakerphoneOn(true);
                    speaker_action_fab.setImageResource(R.drawable.ic_volume_up_white_24dp);
                    isSpeakerPhoneEnabled = true;
                }

            }
        };
    }






    private void retrieveAccessTokenfromServer() {

         StringRequest postRequest = new StringRequest(Request.Method.GET,
                Variables.TWILIO_ACCESS_TOKEN_SERVER +identity+"?roomname="+roomname,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Log.d("resp",response);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            String token=jsonObject.optString("token");

                            VideoActivity.this.accessToken = token;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) ;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.getCache().clear();
        requestQueue.add(postRequest);


    }

    private void configureAudio(boolean enable) {
        if (enable) {
            previousAudioMode = audioManager.getMode();
            // Request audio focus before making any device switch
            requestAudioFocus();
            /*
             * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
             * to be in this mode when playout and/or recording starts for the best
             * possible VoIP performance. Some devices have difficulties with
             * speaker mode if this is not set.
             */
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            /*
             * Always disable microphone mute during a WebRTC call.
             */
            previousMicrophoneMute = audioManager.isMicrophoneMute();
            audioManager.setMicrophoneMute(false);
        } else {
            audioManager.setMode(previousAudioMode);
            audioManager.abandonAudioFocus(null);
            audioManager.setMicrophoneMute(previousMicrophoneMute);
        }
    }

    private void requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            AudioFocusRequest focusRequest =
                    new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(
                                    new AudioManager.OnAudioFocusChangeListener() {
                                        @Override
                                        public void onAudioFocusChange(int i) {
                                        }
                                    })
                            .build();
            audioManager.requestAudioFocus(focusRequest);
        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }




    public void Send_notification(String call_status, String message){

        String name = sharedPreferences.getString(Variables.f_name,"")+" "+sharedPreferences.getString(Variables.l_name,"");

        JSONObject json = new JSONObject();
        try {

            json.put("to", caller_token);
            JSONObject info = new JSONObject();

            info.put("senderId",identity );
            info.put("senderImage","");
            info.put("title", name);
            info.put("body",  message);
            info.put("type",call_type);
            info.put("action",call_status);
            info.put("message",roomname);

            JSONObject ttl=new JSONObject();
            ttl.put("ttl","5s");

            json.put("notification", info);
            json.put("data",info);
            json.put("android",ttl);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Send_Notification(this, json, new Callback() {
            @Override
            public void Responce(String resp) {

            }
        });

    }




    CountDownTimer limit_countDownTimer;
    public void Start_limit_Timer(){
        if(sharedPreferences.getBoolean(Variables.ispuduct_puchase,false) || !Variables.calling_limit) {
        }
        else
            limit_countDownTimer = new CountDownTimer(Variables.max_video_calling_time, 1000) {

                public void onTick(long millisUntilFinished) {
                    if (call_type.equals("video_call")) {
                        int call_time = sharedPreferences.getInt(Variables.video_calling_used_time, 0);
                        sharedPreferences.edit().putInt(Variables.video_calling_used_time, call_time + 1000).commit();
                        if (call_time > Variables.max_video_calling_time) {
                            connectActionFab.performClick();
                        }
                    } else {
                        int call_time = sharedPreferences.getInt(Variables.voice_calling_used_time, 0);
                        sharedPreferences.edit().putInt(Variables.voice_calling_used_time, call_time + 1000).commit();
                        if (call_time > Variables.max_voice_calling_time) {
                            connectActionFab.performClick();
                        }
                    }
                }

                public void onFinish() {
                    connectActionFab.performClick();
                }
            }.start();


    }

    public void Stop_limit_timer(){


        if(limit_countDownTimer!=null){
            limit_countDownTimer.cancel();
        }
    }

}
