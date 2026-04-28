package com.company;

public class AdapterPattern {
    public static void main(String[] args) {
        // Modern MP4 player natively implements MediaPlayer
        MediaPlayer mp4 = new Mp4Player();
        mp4.play("movie.mp4");

        // Adapter makes the old VLC player look like a MediaPlayer — client code is unaware of VLC internals
        MediaPlayer vlc = new VlcPlayerAdapter(new VlcPlayer());
        vlc.play("documentary.vlc");

        // Both work uniformly through the same interface
        playAll(new MediaPlayer[]{ mp4, vlc }, new String[]{ "clip.mp4", "show.vlc" });
    }

    static void playAll(MediaPlayer[] players, String[] files) {
        for (int i = 0; i < players.length; i++) {
            players[i].play(files[i]);
        }
    }
}

// Target interface — what the client code expects
interface MediaPlayer {
    void play(String fileName);
}

// Adaptee — existing class with an incompatible interface that we cannot change
class VlcPlayer {
    void playVlc(String fileName) {
        System.out.println("Playing VLC file: " + fileName);
    }
}

// Object Adapter — wraps the adaptee via composition and translates the call
class VlcPlayerAdapter implements MediaPlayer {
    private final VlcPlayer vlcPlayer;

    VlcPlayerAdapter(VlcPlayer vlcPlayer) { this.vlcPlayer = vlcPlayer; }

    @Override
    public void play(String fileName) {
        vlcPlayer.playVlc(fileName);  // translate MediaPlayer.play() → VlcPlayer.playVlc()
    }
}

// Native target implementation — no adaptation needed
class Mp4Player implements MediaPlayer {
    @Override
    public void play(String fileName) {
        System.out.println("Playing MP4 file: " + fileName);
    }
}

/*
Notes:
1) Adapter pattern converts the interface of a class into another interface the client expects — lets incompatible interfaces work together
2) Also called Wrapper
3) Object Adapter (shown here) uses composition — holds a reference to the adaptee; preferred in Java
4) Class Adapter uses multiple inheritance (extend adaptee + implement target) — only partially possible in Java via interfaces
5) Common uses: integrating third-party libraries, wrapping legacy code, writing test fakes without changing production interfaces
 */
