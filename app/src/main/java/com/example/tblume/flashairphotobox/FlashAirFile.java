package com.example.tblume.flashairphotobox;

import android.support.annotation.NonNull;
import android.text.style.TtsSpan;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class FlashAirFile implements Comparable {

    public String Directory;
    public String FileName;
    public Integer Size;
    public Integer DateNumber;
    public Integer TimeNumber;

    public static FlashAirFile fromString(String str) {

        FlashAirFile file = new FlashAirFile();

        String [] entries = str.split(",");
        file.Directory = entries[0];
        file.FileName = entries[1];
        file.Size = Integer.parseInt(entries[2]);
        file.DateNumber = Integer.parseInt(entries[4]);
        file.TimeNumber = Integer.parseInt(entries[5]);

        return file;
    }

    public Boolean isDirectory() {
        return !FileName.contains(".");
    }

    public Boolean isImageFile() {
         return FileName.toLowerCase().endsWith(".jpg");
    }

    private FlashAirFile() {

    }

    @Override
    public int compareTo(@NonNull Object o) {
        FlashAirFile otherFile = (FlashAirFile)o;

        if (this.DateNumber < otherFile.DateNumber) {
            return -1;
        } else if (this.DateNumber > otherFile.DateNumber) {
            return 1;
        } else {
            if (this.TimeNumber < otherFile.TimeNumber) {
                return -1;
            } else if (this.TimeNumber > otherFile.TimeNumber) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
