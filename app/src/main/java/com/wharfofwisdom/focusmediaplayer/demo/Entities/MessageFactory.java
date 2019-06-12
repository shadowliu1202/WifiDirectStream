package com.wharfofwisdom.focusmediaplayer.demo.Entities;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.UpdateVideo;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

public class MessageFactory {
    public static Message create(Mission mission) {
        if (mission instanceof UpdateVideo) {
            Video video = ((UpdateVideo) mission).getVideo();
            Advertisement advertisement = ((UpdateVideo) mission).getAdvertisement();
            Message mes = new Message(Message.FILE_MESSAGE, advertisement.id(), null, advertisement.video().id());
            MediaFile mediaFile = new MediaFile(video.url(), Message.FILE_MESSAGE);
            mes.setByteArray(mediaFile.fileToByteArray());
            mes.setFileName(mediaFile.getFileName());
            return mes;
        } else {
            return new Message(Message.TEXT_MESSAGE, mission.message(), null, mission.mission());
        }
    }
}
