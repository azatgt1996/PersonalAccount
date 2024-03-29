package com.javamaster.demo.ui.phones;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.javamaster.demo.CustomFragmentListener;
import com.javamaster.demo.FabButtonClick;
import com.javamaster.demo.MainActivity;
import com.javamaster.demo.R;
import com.javamaster.demo.model.Phone;


public class DetailPhoneFragment extends Fragment implements FabButtonClick, CustomFragmentListener {

    private EditText editText_phoneNumber;
    private TextView textView_phoneId;
    private Button deletePhone;
    private Button changePhone;
    private PhonesViewModel phonesViewModel;
    private Phone phone;
    private String oldPhoneNum;
    private Context mContext;
    private View parentLayout;
    private View view;
    private InputMethodManager imm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {

        view = inflater.inflate(R.layout.fragment_detail_phone, container, false);
        ((MainActivity)getActivity()).setListener(this);

        parentLayout = getActivity().findViewById(android.R.id.content).getRootView();
        phonesViewModel = ViewModelProviders.of(getActivity(), new PhonesViewModelFactory(getActivity().getApplication(), mContext, parentLayout)).get(PhonesViewModel.class);
        phonesViewModel.setListener(this);
//        phonesViewModel.openDb();

        editText_phoneNumber = view.findViewById(R.id.phoneNumber);
        textView_phoneId = view.findViewById(R.id.phoneId);

        deletePhone = view.findViewById(R.id.button_deletePhone);
        changePhone = view.findViewById(R.id.button_changePhone);

        deletePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonesViewModel.deleteItem(phone);
                getActivity().onBackPressed();
            }
        });

        changePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changePhone.getText().toString().equals("Change")) {
                    changePhone.setText("Save");
                    editText_phoneNumber.setEnabled(true);
                    editText_phoneNumber.requestFocus();
                    editText_phoneNumber.setSelection(editText_phoneNumber.getText().length());
                    oldPhoneNum = phone.getPhoneNumber();
                    showKeyboard();
                } else {

                    String phoneNum = editText_phoneNumber.getText().toString().trim();

                    if (!phoneNum.equals(oldPhoneNum)) {
                        phone.setPhoneNumber(phoneNum);

                        phonesViewModel.changeItem(phone);
                    } else {
                        changePhone.setText("Change");
                        editText_phoneNumber.setEnabled(false);
                    }
                }
            }
        });

        Bundle bundle = getArguments();

        if (bundle.getBoolean("add")) {
            deletePhone.setVisibility(View.INVISIBLE);
            changePhone.setVisibility(View.INVISIBLE);
            editText_phoneNumber.setText("");
            editText_phoneNumber.setEnabled(true);
            editText_phoneNumber.requestFocus();
//            showKeyboard();
        } else {
            int id = bundle.getInt("phoneId");
            String phoneNum = bundle.getString("phoneNumber");
            phone = new Phone(id, phoneNum);
            editText_phoneNumber.setText(phone.getPhoneNumber());
            deletePhone.setVisibility(View.VISIBLE);
            changePhone.setVisibility(View.VISIBLE);
            textView_phoneId.setText(String.valueOf(phone.getId()));
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onFabClicked() {
        int id = -1;
        String phoneNum = editText_phoneNumber.getText().toString().trim();
        phone = new Phone(id, phoneNum);

        hideKeyboard();
        phonesViewModel.addItem(phone);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        phonesViewModel.closeDb();
    }

    @Override
    public void onBackPressed() {
        getActivity().onBackPressed();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (getArguments().getBoolean("add")) {
            imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view.findFocus(), 0);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCommitted() {
        changePhone.setText("Change");
        editText_phoneNumber.setEnabled(false);
//        getActivity().onBackPressed();
    }

    private void hideKeyboard() {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void showKeyboard() {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
