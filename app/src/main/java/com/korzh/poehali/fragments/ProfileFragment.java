package com.korzh.poehali.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.korzh.poehali.R;
import com.korzh.poehali.activities.PhoneNumberValidation;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;

/**
 * Created by vladimir on 7/4/2014.
 */
public class ProfileFragment extends Fragment {
    public ProfileFragment() {}

    private EditText name = null;
    private EditText cell = null;
    private EditText car = null;
    private EditText plate = null;
    private Spinner  prefix = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        name = (EditText) rootView.findViewById(R.id.edtName);
        prefix = (Spinner) rootView.findViewById(R.id.spinnerCountryCode);
        cell = (EditText) rootView.findViewById(R.id.edtCell);
        car = (EditText) rootView.findViewById(R.id.edtCar);
        plate = (EditText) rootView.findViewById(R.id.edtPlate);

        name.setText(G.getInstance().settings.getString("name",""));
        cell.setText(G.getInstance().settings.getString("cell",""));
        car.setText(G.getInstance().settings.getString("car",""));
        plate.setText(G.getInstance().settings.getString("plate",""));

        prefix.setSelection(G.getInstance().settings.getInt("prefixPos",0));

        return rootView;
    }


    public void saveInfo(){
        SharedPreferences.Editor editor = G.getInstance().settings.edit();

        editor.putString("name", String.valueOf(name.getText()));
        editor.putString("car", String.valueOf(car.getText()));
        editor.putString("plate", String.valueOf(plate.getText()));

        editor.commit();
    }


    public void saveCell(){
        SharedPreferences.Editor editor = G.getInstance().settings.edit();

        editor.putString("prefix", prefix.getSelectedItem().toString());
        editor.putInt("prefixPos", prefix.getSelectedItemPosition());
        editor.putString("cell", String.valueOf(cell.getText()));

        editor.commit();
    }

    private boolean checkCellChanged(){
        String prefix_value = G.getInstance().settings.getString("prefix","");
        String cell_value   = G.getInstance().settings.getString("cell","");

        boolean result = !(prefix_value.equals(prefix.getSelectedItem().toString()) &&
                cell_value.equals(cell.getText().toString()));

        U.Log(getClass().getSimpleName(),"saved prefix: "+prefix_value+" saved cell: "+cell_value+" res: "+result);
        return result;
    }

    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.btnSaveInfo:
                if (checkCellChanged()){
                    Intent i = new Intent(getActivity(), PhoneNumberValidation.class);
                    Bundle b = new Bundle();
                    b.putString("number", prefix.getSelectedItem().toString()+cell.getText().toString());
                    i.putExtras(b);
                    startActivityForResult(i, C.REQUEST_CODE_VALIDATE_PHONE);
                }
                saveInfo();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        U.Log(getClass().getSimpleName(), "onActivityResult " + requestCode + " " + resultCode);
        if (requestCode == C.REQUEST_CODE_VALIDATE_PHONE) {
            if(resultCode == Activity.RESULT_OK){
                saveCell();
                Toast.makeText(getActivity(), getActivity().getString(R.string.toast_number_confirmed), Toast.LENGTH_LONG).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                cellNotConfirmed();
                Toast.makeText(getActivity(), getActivity().getString(R.string.toast_number_confirmation_error), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void cellNotConfirmed() {
        cell.setText("");
    }
}
