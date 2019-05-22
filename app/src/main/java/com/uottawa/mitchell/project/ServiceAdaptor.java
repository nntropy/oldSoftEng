package com.uottawa.mitchell.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//Creating custom adaptor so we can pass in Service objects into the listView but however only the name will appear for now
public class ServiceAdaptor extends ArrayAdapter<Service> implements View.OnClickListener{
    ServiceAdaptor (Context context, ArrayList<Service> serviceList){
        super (context,0,serviceList);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Service service= getItem (position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_service, parent, false);
        }
        TextView serviceName= convertView.findViewById(R.id.serviceInfo);
        serviceName.setText(service.getName());
        return convertView;
    }
    //Setting onClick for custom adaptor
    public void onClick (View v){
        int position= (Integer) v.getTag();
        Object object=getItem(position);
        Service service = (Service) object;
    }
}
