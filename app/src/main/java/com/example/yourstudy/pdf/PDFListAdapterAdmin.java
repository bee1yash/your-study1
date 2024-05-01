package com.example.yourstudy.pdf;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yourstudy.R;

import java.util.List;

public class PDFListAdapterAdmin extends ArrayAdapter<putPDF> {

    private Context mContext;
    private List<putPDF> mPDFList;

    public PDFListAdapterAdmin(Context context, List<putPDF> pdfList) {
        super(context, 0, pdfList);
        mContext = context;
        mPDFList = pdfList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.retrieve_pdf_admin, parent, false);
        }

        putPDF currentPDF = mPDFList.get(position);

        TextView fileNameTextView = listItem.findViewById(R.id.text_file_name);
        fileNameTextView.setText(currentPDF.getName());

        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileUrl = currentPDF.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(fileUrl), "*/*");
                    mContext.startActivity(intent);
            }
        });

        return listItem;
    }
}
