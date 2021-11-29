/**
 * EasyPlex - Movies - Live Streaming - TV Series, Anime
 *
 * @author @Y0bEX
 * @package  EasyPlex - Movies - Live Streaming - TV Series, Anime
 * @copyright Copyright (c) 2021 Y0bEX,
 * @license http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile https://codecanyon.net/user/yobex
 * @link yobexd@gmail.com
 * @skype yobexd@gmail.com
 **/
 
 
package com.beeecorptv.ui.downloadmanager.ui.adddownload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.beeecorptv.R;
import com.beeecorptv.ui.downloadmanager.core.model.data.entity.UserAgent;

public class UserAgentAdapter extends ArrayAdapter<UserAgent>
{
    private DeleteListener deleteListener;

    public UserAgentAdapter(@NonNull Context context, DeleteListener deleteListener)
    {
        super(context, R.layout.spinner_user_agent_item);

        this.deleteListener = deleteListener;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent)
    {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_user_agent_item, parent, false);
        }

        UserAgent userAgent = getItem(position);
        if (userAgent != null) {
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(userAgent.userAgent);
        }

        /* Ignore read only system agents (e.g system user agent) and null */
        ImageView deleteButton = view.findViewById(R.id.delete);
        if (userAgent != null && !userAgent.readOnly)
            deleteButton.setOnClickListener((View v) -> {
                if (deleteListener != null)
                    deleteListener.onDelete(userAgent);
            });
        else
            deleteButton.setVisibility(View.GONE);

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_user_agent_view, parent, false);
        }

        UserAgent userAgent = getItem(position);
        if (userAgent != null) {
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(userAgent.userAgent);
        }

        return view;
    }

    public interface DeleteListener
    {
        void onDelete(UserAgent userAgent);
    }
}
