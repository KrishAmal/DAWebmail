package com.sigmobile.dawebmail.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sigmobile.dawebmail.R;
import com.sigmobile.dawebmail.ViewEmail;
import com.sigmobile.dawebmail.database.EmailMessage;
import com.sigmobile.dawebmail.utils.Constants;
import com.sigmobile.dawebmail.utils.Printer;
import com.sigmobile.dawebmail.utils.TheFont;

import java.util.ArrayList;

/**
 * Created by rish on 6/10/15.
 */
public class MailAdapter extends BaseAdapter {

    ArrayList<EmailMessage> emails;
    Context context;
    private ArrayList<EmailMessage> emailsToDelete;
    DeleteSelectedListener deleteSelectedListener;
    boolean clickedForDelete[];

    public MailAdapter(ArrayList<EmailMessage> emails, Context context, DeleteSelectedListener deleteSelectedListener) {
        this.context = context;
        emailsToDelete = new ArrayList<>();
        this.emails = emails;
        this.deleteSelectedListener = deleteSelectedListener;
        this.clickedForDelete = new boolean[emails.size()];
    }

    @Override
    public int getCount() {
        return emails.size();
    }

    @Override
    public EmailMessage getItem(int position) {
        return emails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.element_email, null);
            new ViewHolder(convertView);
        }
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final EmailMessage item = getItem(position);

        holder.tv_name.setTextSize(15);

        if (clickedForDelete[position]) {
            System.out.println("" + item.fromName + " " + item.subject + " is marked for delete");
        }

        if (item.readUnread.equals(Constants.WEBMAIL_UNREAD)) {
            if (!clickedForDelete[position]) {
                holder.iv_icon.setImageResource(R.drawable.final_unread);
            } else {
                System.out.println("set holder icon to delete wala");
                holder.iv_icon.setAnimation(AnimationUtils.loadAnimation(context, R.anim.abc_grow_fade_in_from_bottom));
                holder.iv_icon.setImageResource(R.drawable.ic_action_delete_red);
            }

            holder.tv_name.setTypeface(null, Typeface.BOLD);
        } else {

            if (!clickedForDelete[position]) {
                holder.iv_icon.setImageResource(R.drawable.final_read);
            } else {
                Printer.println("set holder icon to delete wala");
                holder.iv_icon.setImageResource(R.drawable.ic_action_delete_red);
                holder.iv_icon.setAnimation(AnimationUtils.loadAnimation(context, R.anim.abc_grow_fade_in_from_bottom));
            }
            holder.tv_name.setTypeface(null, Typeface.NORMAL);
        }

        holder.tv_name.setText(item.fromName);
        holder.tv_date.setText(item.dateInMillis);
        holder.tv_subject.setText(item.subject);

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                Printer.println("Long click");
                if (clickedForDelete[position]) {
                    clickedForDelete[position] = false;
                    notifyDataSetChanged();
                    emailsToDelete.remove(item);
                    System.out.println("clicked for delete is true, returning to normal");
                } else {
                    clickedForDelete[position] = true;
                    emailsToDelete.add(item);
                    notifyDataSetChanged();
                    Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(20);
                    System.out.println("clicked for delete is false, added to emailsToDelete");
                }
                deleteSelectedListener.onItemClickedForDelete(emailsToDelete);
                return true;
            }
        });

        holder.iv_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (clickedForDelete[position]) {
                    clickedForDelete[position] = false;
                    notifyDataSetChanged();
                    emailsToDelete.remove(item);
                    Printer.println("clicked for delete is true, returning to normal");

                } else {
                    clickedForDelete[position] = true;
                    emailsToDelete.add(item);
                    notifyDataSetChanged();
                    Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(20);
                    Printer.println("clicked for delete is false, added to emailsToDelete");
                }
                deleteSelectedListener.onItemClickedForDelete(emailsToDelete);
            }
        });

        holder.tv_relaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewEmail.class);
                intent.putExtra(Constants.CURRENT_EMAIL_ID, emails.get(position).contentID);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name, tv_subject, tv_date;
        LinearLayout tv_relaLayout;

        public ViewHolder(View view) {
            tv_relaLayout = (LinearLayout) view.findViewById(R.id.tv_relativelayout);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_subject = (TextView) view.findViewById(R.id.tv_subject);

            Typeface font = TheFont.getFont(context);

            tv_date.setTypeface(font);
            tv_name.setTypeface(font);
            tv_subject.setTypeface(font);

            view.setTag(this);
        }
    }

    public interface DeleteSelectedListener {
        void onItemClickedForDelete(ArrayList<EmailMessage> emailsToDelete);
    }
}