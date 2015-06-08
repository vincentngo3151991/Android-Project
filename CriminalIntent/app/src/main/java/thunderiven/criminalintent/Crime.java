package thunderiven.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Vincent Ngo on 5/29/2015.
 */
public class Crime {
    private static final String JSON_ID="id";
    private static final String JSON_TITLE="title";
    private static final String JSON_SOLVED="solved";
    private static final String JSON_DATE="date";
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public UUID getId() {
        return mId;
    }

    public Crime() {
        mId=UUID.randomUUID();
        mDate=new Date();

    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public String getDateString() {
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT).format(mDate);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json=new JSONObject();
        json.put(JSON_ID,mId.toString());
        json.put(JSON_TITLE,mTitle);
        json.put(JSON_SOLVED,mSolved);
        json.put(JSON_DATE,mDate.getTime());
        return json;
    }

    public Crime(JSONObject json) throws JSONException {
        mId=UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_TITLE)) {
            mTitle=json.getString(JSON_TITLE);
        }
        mSolved=json.getBoolean(JSON_SOLVED);
        mDate=new Date(json.getLong(JSON_DATE));
    }
}
