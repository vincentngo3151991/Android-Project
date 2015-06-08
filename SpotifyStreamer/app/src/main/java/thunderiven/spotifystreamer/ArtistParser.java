package thunderiven.spotifystreamer;

import android.util.Log;

import kaaes.spotify.webapi.android.models.Image;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Vincent Ngo on 6/2/2015.
 */
public  class ArtistParser {
    // This class is used to get the name, spotifyId, and the thumbnail url from an artist class
    // It includes the getters and the constructor that accepts an artist as an input
    private String mName;
    private  String mId;
    private String mThumbnailId;

    public String getName() {
        return mName;
    }

    public String getId() {
        Log.d("ArtistParser",mId);
        return mId;
    }

    public String getThumbnailId() {
        return mThumbnailId;
    }

    // Public constructor that accepts artist as an input
    public ArtistParser(Artist artist) {
        mName=artist.name;
        mId=artist.id;
        List<Image> imageList=artist.images;
        if (!imageList.isEmpty()) {
//            if the list of an image is not empty, then we get the first image from the image list
//            as a thumbnail
            mThumbnailId=imageList.get(0).url;
        } else {
            mThumbnailId="https://www.facebook.com/images/photos/empty-album.png";
        }
    }
}
