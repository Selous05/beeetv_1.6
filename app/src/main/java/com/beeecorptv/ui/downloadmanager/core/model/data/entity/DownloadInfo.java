/*
 * EasyPlex - Movies - Live Streaming - TV Series, Anime
 *
 * @author @Y0bEX
 * @package EasyPlex - Movies - Live Streaming - TV Series, Anime
 * @copyright Copyright (c) 2021 Y0bEX,
 * @license http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile https://codecanyon.net/user/yobex
 * @link yobexd@gmail.com
 * @skype yobexd@gmail.com
 **/

package com.beeecorptv.ui.downloadmanager.core.model.data.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.beeecorptv.ui.downloadmanager.core.model.data.StatusCode;
import com.beeecorptv.ui.downloadmanager.core.storage.converter.UriConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/*
 * The class encapsulates information about download.
 */

@Entity
public class DownloadInfo implements Parcelable, Comparable<DownloadInfo>
{
    /* Piece number can't be less or equal zero */
    public static final int MIN_PIECES = 1;
    /* Recommended max number of pieces */
    public static final int MAX_PIECES = 16;
    /*
     * This download is visible but only shows in the notifications
     * while it's in progress
     */
    public static final int VISIBILITY_VISIBLE = 0;
    /*
     * This download is visible and shows in the notifications while
     * in progress and after completion
     */
    public static final int VISIBILITY_VISIBLE_NOTIFY_COMPLETED = 1;
    /* This download doesn't show in the notifications */
    public static final int VISIBILITY_HIDDEN = 2;
    /* This download shows in the notifications after completion ONLY */
    public static final int VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION = 3;
    /*
     * The minimum amount of time that the download manager accepts for
     * a Retry-After response header with a parameter in delta-seconds
     */
    public static final int MIN_RETRY_AFTER = 30; /* 30 s */
    /*
     * The maximum amount of time that the download manager accepts for
     * a Retry-After response header with a parameter in delta-seconds
     */
    public static final int MAX_RETRY_AFTER = 24 * 60 * 60; /* 24 h */


    @PrimaryKey
    @NonNull
    public UUID id;
    /*
     * SAF or filesystem storage
     * (with content:// or file:// scheme respectively)
     */
    @TypeConverters({UriConverter.class})
    @NonNull
    public Uri dirPath;
    @NonNull
    public String url;


    @NonNull
    public String fileName;

    @Nullable
    public String mediaName;

    @Nullable
    public String mediaBackdrop;

    @Nullable
    public String mediaId;

    @Nullable
    public String mediatype;

    @Nullable
    public String refer;


    public String description;
    public String mimeType = "application/octet-stream";
    public long totalBytes = -1;
    private int numPieces = MIN_PIECES;
    public int statusCode = StatusCode.STATUS_PENDING;
    public boolean unmeteredConnectionsOnly = false;
    public boolean retry = true;
    /* Indicates that server support partial download */
    public boolean partialSupport = true;
    public String statusMsg;
    public long dateAdded;
    public int visibility = VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
    /* E.g. MIME-type, size, headers, etc */
    public boolean hasMetadata = true;
    public String userAgent;
    public int numFailed = 0;
    /* In ms */
    public int retryAfter;
    public long lastModify;
    /* MD5, SHA-256 */
    public String checksum;

    public DownloadInfo(@NonNull Uri dirPath,
                        @NonNull String url,
                        @NonNull String fileName,@Nullable String mediaName,@NonNull String mediatype,@NonNull String mediaId,@NonNull String mediaBackdrop,@NonNull String refer)
    {
        this.id = UUID.randomUUID();
        this.dirPath = dirPath;
        this.url = url;
        this.fileName = fileName;
        this.mediaName = mediaName;
        this.mediatype = mediatype;
        this.mediaId = mediaId;
        this.mediaBackdrop = mediaBackdrop;
        this.refer=refer;
    }

    @Ignore
    public DownloadInfo(@NonNull Parcel source)
    {
        id = (UUID)source.readSerializable();
        dirPath = source.readParcelable(Uri.class.getClassLoader());
        url = source.readString();
        fileName = source.readString();
        mediaName = source.readString();
        mediaId = source.readString();
        mediatype = source.readString();
        refer = source.readString();
        mediaBackdrop = source.readString();
        description = source.readString();
        mimeType = source.readString();
        totalBytes = source.readLong();
        statusCode = source.readInt();
        unmeteredConnectionsOnly = source.readByte() > 0;
        numPieces = source.readInt();
        retry = source.readByte() > 0;
        statusMsg = source.readString();
        dateAdded = source.readLong();
        visibility = source.readInt();
        hasMetadata = source.readByte() > 0;
        userAgent = source.readString();
        numFailed = source.readInt();
        retryAfter = source.readInt();
        lastModify = source.readLong();
        checksum = source.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeSerializable(id);
        dest.writeParcelable(dirPath, flags);
        dest.writeString(url);
        dest.writeString(fileName);
        dest.writeString(mediaId);
        dest.writeString(mediaName);
        dest.writeString(mediatype);
        dest.writeString(refer);
        dest.writeString(mediaBackdrop);
        dest.writeString(description);
        dest.writeString(mimeType);
        dest.writeLong(totalBytes);
        dest.writeInt(statusCode);
        dest.writeByte((byte)(unmeteredConnectionsOnly ? 1 : 0));
        dest.writeInt(numPieces);
        dest.writeByte((byte)(retry ? 1 : 0));
        dest.writeString(statusMsg);
        dest.writeLong(dateAdded);
        dest.writeInt(visibility);
        dest.writeByte((byte)(hasMetadata ? 1 : 0));
        dest.writeString(userAgent);
        dest.writeInt(numFailed);
        dest.writeInt(retryAfter);
        dest.writeLong(lastModify);
        dest.writeString(checksum);
    }

    public static final Creator<DownloadInfo> CREATOR =
            new Creator<DownloadInfo>()
            {
                @Override
                public DownloadInfo createFromParcel(Parcel source)
                {
                    return new DownloadInfo(source);
                }

                @Override
                public DownloadInfo[] newArray(int size)
                {
                    return new DownloadInfo[size];
                }
            };

    public void setNumPieces(int numPieces)
    {
        if (numPieces <= 0)
            throw new IllegalArgumentException("Piece number can't be less or equal zero");

        if (!partialSupport && numPieces > 1)
            throw new IllegalStateException("The download doesn't support partial download");

        if ((totalBytes <= 0 && numPieces != 1) || (totalBytes > 0 && totalBytes < numPieces))
            throw new IllegalStateException("The number of pieces can't be more than the number of total bytes");

        this.numPieces = numPieces;
    }

    public int getNumPieces()
    {
        return numPieces;
    }

    public List<DownloadPiece> makePieces()
    {
        List<DownloadPiece> pieces = new ArrayList<>();
        long piecesSize = -1;
        long lastPieceSize = -1;
        if (totalBytes != -1) {
            piecesSize = totalBytes / numPieces;
            lastPieceSize = piecesSize + totalBytes % numPieces;
        }

        long curBytes = 0;
        for (int i = 0; i < numPieces; i++) {
            long pieceSize = (i == numPieces - 1 ? lastPieceSize : piecesSize);
            pieces.add(new DownloadPiece(id, i, pieceSize, curBytes));
            curBytes += pieceSize;
        }

        return pieces;
    }

    public long pieceStartPos(@NonNull DownloadPiece piece)
    {
        if (totalBytes <= 0)
            return 0;

        long pieceSize = totalBytes / numPieces;

        return piece.index * pieceSize;
    }

    public long pieceEndPos(@NonNull DownloadPiece piece)
    {
        if (piece.size <= 0)
            return -1;

        return pieceStartPos(piece) + piece.size - 1;
    }

    public long getDownloadedBytes(@NonNull DownloadPiece piece)
    {
        return piece.curBytes - pieceStartPos(piece);
    }

    @Override
    public int compareTo(@NonNull DownloadInfo another)
    {
        return fileName.compareTo(another.fileName);
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof DownloadInfo))
            return false;

        if (o == this)
            return true;

        DownloadInfo info = (DownloadInfo)o;

        return id.equals(info.id) &&
                dirPath.equals(info.dirPath) &&
                url.equals(info.url) &&
                fileName.equals(info.fileName) &&
                (description == null || description.equals(info.description)) &&
                (mimeType == null || mimeType.equals(info.mimeType)) &&
                totalBytes == info.totalBytes &&
                numPieces == info.numPieces &&
                statusCode == info.statusCode &&
                unmeteredConnectionsOnly == info.unmeteredConnectionsOnly &&
                retry == info.retry &&
                partialSupport == info.partialSupport &&
                (statusMsg == null || statusMsg.equals(info.statusMsg)) &&
                dateAdded == info.dateAdded &&
                visibility == info.visibility &&
                (userAgent == null || userAgent.equals(info.userAgent)) &&
                numFailed == info.numFailed &&
                retryAfter == info.retryAfter &&
                lastModify == info.lastModify &&
                (checksum == null || checksum.equals(info.checksum));
    }

    @Override
    public String toString()
    {
        return "DownloadInfo{" +
                "id=" + id +
                ", dirPath=" + dirPath +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", mediaName='" + mediaName + '\'' +
                ", mediatype='" + mediatype + '\'' +
                ", description='" + description + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", totalBytes=" + totalBytes +
                ", numPieces=" + numPieces +
                ", statusCode=" + statusCode +
                ", unmeteredConnectionsOnly=" + unmeteredConnectionsOnly +
                ", retry=" + retry +
                ", partialSupport=" + partialSupport +
                ", statusMsg='" + statusMsg + '\'' +
                ", dateAdded=" + SimpleDateFormat.getDateTimeInstance().format(new Date(dateAdded)) +
                ", visibility=" + visibility +
                ", hasMetadata=" + hasMetadata +
                ", userAgent=" + userAgent +
                ", numFailed=" + numFailed +
                ", retryAfter=" + retryAfter +
                ", lastModify=" + lastModify +
                ", checksum=" + checksum +
                '}';
    }
}