package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.j256.ormlite.stmt.PreparedQuery;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.FilledOutFormListAdapter;
import org.secfirst.umbrella.adapters.FormListAdapter;
import org.secfirst.umbrella.models.Form;
import org.secfirst.umbrella.models.FormItem;
import org.secfirst.umbrella.models.FormOption;
import org.secfirst.umbrella.models.FormScreen;
import org.secfirst.umbrella.models.FormValue;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.IOCipherContentProvider;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import info.guardianproject.iocipher.File;
import info.guardianproject.iocipher.FileWriter;
import info.guardianproject.iocipher.VirtualFileSystem;
import timber.log.Timber;

import static org.secfirst.umbrella.util.IOCipherContentProvider.SHARE_FILE;

public class TabbedFormsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, FilledOutFormListAdapter.OnSessionHTMLCreate {

    private FormListAdapter formListAdapter;
    private FilledOutFormListAdapter filledOutAdapter;
    Global global;
    List<Form> allForms = new ArrayList<>();
    List<FormValue> fValues = new ArrayList<>();
    SwipeRefreshLayout mSwipeRefresh;
    LinearLayout filledOutHolder;
    private ProgressDialog progressDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        global = (Global) context.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_forms,
                container, false);

        filledOutHolder = (LinearLayout) rootView.findViewById(R.id.filled_out_holder);

        RecyclerView formListView = (RecyclerView) rootView.findViewById(R.id.form_list);
        RecyclerView filledOutList = (RecyclerView) rootView.findViewById(R.id.filled_out_list);

        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefresh.setOnRefreshListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        formListView.setLayoutManager(llm);

        formListAdapter = new FormListAdapter(getContext());

        formListView.setAdapter(formListAdapter);

        LinearLayoutManager llm1 = new LinearLayoutManager(getContext());
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        formListView.setLayoutManager(llm1);

        filledOutAdapter = new FilledOutFormListAdapter(getContext(), this);
        filledOutList.setAdapter(filledOutAdapter);
        LinearLayoutManager llm2 = new LinearLayoutManager(getContext());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        filledOutList.setLayoutManager(llm2);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        }, 800);
    }

    @Override
    public void onStop() {
        if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
        super.onStop();
    }

    @Override
    public void onRefresh() {
        try {
            allForms = global.getDaoForm().queryForAll();
            PreparedQuery<FormValue> queryBuilder = global.getDaoFormValue().queryBuilder().groupBy(FormValue.FIELD_SESSION).prepare();
            fValues = global.getDaoFormValue().query(queryBuilder);
            filledOutHolder.setVisibility((fValues==null || fValues.size()<1) ? View.GONE : View.VISIBLE);
            formListAdapter.updateData(allForms);
            filledOutAdapter.updateData(fValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onSessionHTMLCreated(final long sessionId) {
        progressDialog = UmbrellaUtil.launchRingDialogWithText((Activity) getContext(), "");
        List<FormValue> formValues = null;
        Form form = null;
        try {
            PreparedQuery<FormValue> query = global.getDaoFormValue().queryBuilder().where().eq(FormValue.FIELD_SESSION, sessionId).prepare();
            formValues = global.getDaoFormValue().query(query);
            if (formValues!=null && formValues.size()>0) {
                int formId = formValues.get(0).getFormItem(global).getFormScreen(global).getForm(null).get_id();
                form = global.getDaoForm().queryForId(String.valueOf(formId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (form!=null && formValues.size()>0) {

            final Form finalForm = form;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    String fileName = finalForm.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
                    if (fileName.length() > 50) fileName = fileName.substring(0, 50);
                    final Document doc = Jsoup.parse("");
                    Element head = doc.head();
                    head.append("<meta charset='UTF-8'>");
                    Element body = doc.body();
                    body.attr("style", "display:block;width:100%;");
                    doc.title(fileName);
                    body.append("<h1>"+ finalForm.getTitle()+"</h1>");
                    for (FormScreen screen : finalForm.getScreens()) {
                        body.append("<h3>"+screen.getTitle()+"</h3>");
                        body.append("<form>");
                        for (FormItem formItem : screen.getItems()) {
                            try {
                                PreparedQuery<FormValue> queryBuilder = global.getDaoFormValue().queryBuilder().where().eq(FormValue.FIELD_SESSION, sessionId).and().eq(FormValue.FIELD_FORM_ITEM_ID, formItem.get_id()).prepare();
                                formItem.setValues(global.getDaoFormValue().query(queryBuilder));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            Element paragraph = body.append("<p></p>");
                            paragraph.append("<h5>"+formItem.getTitle()+"</h5>");
                            switch (formItem.getType()) {
                                case "text_input":
                                    String textInput = "";
                                    if (formItem.getValues()!=null && formItem.getValues().size()>0) textInput = formItem.getValues().get(0).getValue();
                                    paragraph.append("<input type='text' value='"+textInput+"' readonly />");
                                    break;
                                case "text_area":
                                    String textArea = "";
                                    if (formItem.getValues()!=null && formItem.getValues().size()>0) textArea = formItem.getValues().get(0).getValue();
                                    paragraph.append("<textarea rows='4' cols='50' readonly>"+textArea+"</textarea>");
                                    break;
                                case "multiple_choice":
                                    List<String> mcValList = new ArrayList<>();
                                    if (formItem.getValues()!=null && formItem.getValues().size()>0 && !formItem.getValues().get(0).getValue().equals("")) {
                                        mcValList = new LinkedList<>(Arrays.asList(formItem.getValues().get(0).getValue().split(",")));
                                    }
                                    for (FormOption formOption : formItem.getOptions()) {
                                        boolean isChecked = false;
                                        if (mcValList.size()>0) {
                                            isChecked = Boolean.parseBoolean(mcValList.get(0));
                                            mcValList.remove(0);
                                        }
                                        paragraph.append("<label><input type='checkbox' "+ (isChecked?"checked":"" )+" readonly>"+formOption.getOption()+"</label><br>");
                                    }
                                    break;
                                case "single_choice":
                                    List<String> scValList = new ArrayList<>();
                                    if (formItem.getValues()!=null && formItem.getValues().size()>0 && !formItem.getValues().get(0).getValue().equals("")) {
                                        scValList = new LinkedList<>(Arrays.asList(formItem.getValues().get(0).getValue().split(",")));
                                    }
                                    for (FormOption formOption : formItem.getOptions()) {
                                        boolean isChecked = false;
                                        if (scValList.size()>0) {
                                            isChecked = Boolean.parseBoolean(scValList.get(0));
                                            scValList.remove(0);
                                        }
                                        paragraph.append("<label><input type='radio' "+ (isChecked?"checked":"" )+" readonly>"+formOption.getOption()+"</label><br>");
                                    }
                                    break;
                                case "toggle_button":
                                    String toggleText = "";
                                    if (formItem.getValues()!=null && formItem.getValues().size()>0) toggleText = formItem.getValues().get(0).getValue();
                                    paragraph.append("<br><span>"+toggleText+"</span><br>");
                                    break;
                            }
                        }
                        body.append("</form>");
                    }

                    java.io.File f = new java.io.File(Global.INSTANCE.getCacheDir(), SHARE_FILE);
                    if (!f.exists()) {
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    String path = f.getPath();

                    VirtualFileSystem vfs = VirtualFileSystem.get();
                    vfs.setContainerPath(path);
                    try {
                        if (!vfs.isMounted()) vfs.mount(global.getOrmHelper().getPassword());
                        if (!vfs.isMounted()) vfs.createNewContainer(path, global.getOrmHelper().getPassword());

                        File file = new File("/", fileName +".html");
                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                            writer.write(doc.toString());
                            writer.flush();
                            writer.close();
                            Uri uri = Uri.parse(IOCipherContentProvider.FILES_URI + file.getName());
                            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                            intentShareFile.setType("text/html");
                            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
                            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                    getString(R.string.share_form));
                            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(intentShareFile, getString(R.string.share_form)));

//                Intent onlyView = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "text/html");
//                onlyView.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                startActivity(onlyView);
                        } catch (IOException e) {
                            Timber.e(e);
                        } finally {
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        }
                    } catch (IllegalStateException | IllegalArgumentException e) {
                        Timber.e(e);
                    }
                }
            });
        }
    }

}
