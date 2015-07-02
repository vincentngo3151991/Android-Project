package thunderiven.spotifystreamer;

import java.util.List;

import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Vincent Ngo on 6/2/2015.
 */
public class TrackParser {
    /* this class is used to get the name, album name, preview url, large and small thumbnail url
    * from a track. It contains public getter method and accept Track as an input
    * */
    private String mArtistName;
    private String mName;
    private String mAlbumName;
    private String mPreviewUrl;
    private String mThumbnailLarge;

    public String getArtistName() {
        return mArtistName;
    }

    public String getName() {
        return mName;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    public String getThumbnailLarge() {
        return mThumbnailLarge;
    }

    public String getThumbnailSmall() {
        return mThumbnailSmall;
    }

    private String mThumbnailSmall;
    public TrackParser(Track track) {
        List<ArtistSimple> artists=track.artists;
        StringBuilder builder=new StringBuilder();
        for (int i=0;i<artists.size();i++) {
            builder.append(artists.get(i).name);
            // Add comma to the artist list
            if (artists.size()>1 || i!=artists.size()-1) {
                builder.append(", ");
            }
        }

        mArtistName=builder.toString();

        mName=track.name;
        mPreviewUrl=track.preview_url;
        AlbumSimple album=track.album;
        mAlbumName=album.name;
        List<Image> list=album.images;
        if (!list.isEmpty()) {
            /* Similar to ArtistParser class, if the list of image is empty, the thumbnail url will
            be set to an empty album. If the list contain only 1 image, set both large and small
            thumbnail to the image. Otherwise set the large thumbnail to 600 by 600 and small
            thumbnail to 300 by 300
            * */
            if (list.size()>1) {
                mThumbnailLarge=list.get(0).url;
                mThumbnailSmall=list.get(1).url;
            } else if (list.size()==1) {
                mThumbnailLarge=list.get(0).url;
                mThumbnailSmall=list.get(0).url;
            }
        } else {
            mThumbnailLarge="https://www.facebook.com/images/photos/empty-album.png";
            mThumbnailSmall="https://www.facebook.com/images/photos/empty-album.png";
        }
    }
}
