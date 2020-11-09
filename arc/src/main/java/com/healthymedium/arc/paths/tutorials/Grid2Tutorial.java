package com.healthymedium.arc.paths.tutorials;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.TutorialTemplate;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.Grid2BoxView;
import com.healthymedium.arc.ui.Grid2ChoiceDialog;
import com.healthymedium.arc.ui.Grid2LetterView;
import com.healthymedium.arc.ui.base.PointerDrawable;
import com.healthymedium.arc.utilities.ViewUtil;

public class Grid2Tutorial extends TutorialTemplate {

    public static final String HINT_PREVENT_TUTORIAL_CLOSE_GRIDS = "HINT_PREVENT_TUTORIAL_CLOSE_GRIDS";

    boolean phoneSelected = false;
    boolean keySelected = false;
    boolean penSelected = false;
    boolean othersReady = false;

    boolean userMovedOrRemoved = false;
    boolean mechanicsHintShown = false;
    HintPointer mechanicsHint;

    View items;
    RelativeLayout itemsLayout;

    View letters;
    GridLayout letterLayout;

    View grids;
    GridLayout gridLayout;

    Button continueButton;
    TextView gridTextView;
    TextView gridHintTextView;

    HintPointer remindMeHint;
    HintHighlighter remindMeHighlight;
    HintPointer otherItemsHint;

    Grid2ChoiceDialog dialog;
    Grid2BoxView.Listener boxViewListener;
    Grid2ChoiceDialog.Listener dialogListener;

    public Grid2Tutorial() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(getContext());

        items = inflater.inflate(R.layout.fragment_grid2_tutorial_items, container, false);
        itemsLayout = items.findViewById(R.id.itemsLayout);

        grids = inflater.inflate(R.layout.fragment_grid2_test, container, false);
        gridLayout = grids.findViewById(R.id.gridLayout);
        continueButton = grids.findViewById(R.id.buttonContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userMovedOrRemoved) {
                    launchMechanicsTutorial();
                    return;
                }

                incrementProgress();
                fadeOutView(grids);
                showComplete();
            }
        });

        gridHintTextView = grids.findViewById(R.id.tapGridText);
        gridHintTextView.setVisibility(View.INVISIBLE);

        gridTextView = grids.findViewById(R.id.tapGridText);
        gridTextView.setText(Html.fromHtml("Place each item in its location<br>from part one."));

        letters = inflater.inflate(R.layout.fragment_grid2_letters, container, false);
        letterLayout = letters.findViewById(R.id.gridLayout);

        adjustLayouts();

        remindMeHint = new HintPointer(getActivity(), getGridView(4, 3), false, true);
        register(remindMeHint);

        remindMeHighlight = new HintHighlighter(getActivity());
        register(remindMeHighlight);

    }

    @Override
    protected String getClosePreventionHintTag() {
        return HINT_PREVENT_TUTORIAL_CLOSE_GRIDS;
    }

    @Override
    protected int getProgressIncrement() {
        return 25;
    }

    private Grid2BoxView getGridView(int row, int col) {
        return (Grid2BoxView) gridLayout.getChildAt((gridLayout.getColumnCount() * row) + col);
    }

    private Grid2LetterView getLetterView(int row, int col) {
        return (Grid2LetterView) letterLayout.getChildAt((letterLayout.getColumnCount() * row) + col);
    }

    private void adjustLayouts() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        int auxHeight = ViewUtil.dpToPx(124);
        int viewHeight = displayHeight - ViewUtil.getStatusBarHeight() - ViewUtil.getNavBarHeight() - auxHeight;

        float aspectRatio = ((float) displayWidth) / ((float) viewHeight);

        int lettersHeight = viewHeight;
        int gridsHeight = viewHeight;

        if (aspectRatio < 0.75f) {
            lettersHeight = (int) (displayWidth / 0.75f);
        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (0.75f * gridsHeight), LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = ViewUtil.dpToPx(26);
            layoutParams.bottomMargin = ViewUtil.dpToPx(2);
            layoutParams.gravity = Gravity.CENTER;
            gridLayout.setLayoutParams(layoutParams);
        }

        lettersHeight -= -ViewUtil.dpToPx(16);

        float letterRatio = ((float) letterLayout.getColumnCount()) / ((float) letterLayout.getRowCount());
        int lettersWidth = (int) (letterRatio * (lettersHeight));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(lettersWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = ViewUtil.dpToPx(28);
        layoutParams.bottomMargin = ViewUtil.dpToPx(4);
        layoutParams.gravity = Gravity.CENTER;
        letterLayout.setLayoutParams(layoutParams);
    }


    @Override
    protected void setupInitialLayout() {
        setupStudySection();
    }

    private void setupStudySection() {
        getGridView(3, 0).setImage(R.drawable.pen);
        getGridView(1, 1).setImage(R.drawable.phone);
        getGridView(2, 3).setImage(R.drawable.key);
        fadeInView(grids);

        final HintPointer gridsHint = new HintPointer(getActivity(), gridLayout, true);
        register(gridsHint);

        gridsHint.setText("This test has three parts. In the first part, you'll be asked to <b>remember the location</b> of three items. The items will be placed in a grid of boxes. <b>Remember which box each item is in.</b> You will have 3 seconds to study the locations.");
        gridsHint.getShadow().addTarget(progressBar);
        gridsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridsHint.dismiss();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Run after the user has studied the initial layout of the items in the grid
                        // Advances the user to setInitialLetterLayout(), the letter tapping test

                        incrementProgress();

                        final HintPointer partTwoHint = new HintPointer(getActivity(), gridLayout, true);
                        partTwoHint.getShadow().addTarget(progressBar);
                        register(partTwoHint);

                        partTwoHint.setText(ViewUtil.getString(R.string.popup_tutorial_part2));
                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                partTwoHint.dismiss();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setupLetterLayout();
                                    }
                                }, 600);
                            }
                        };
                        partTwoHint.addButton(ViewUtil.getString(R.string.button_next), listener);
                        partTwoHint.show();

                    }
                }, 3000);
            }
        });
        gridsHint.show();
    }


    // Displays the letters layout and prompts the user to tap a specific letter F
    private void setupLetterLayout() {

        getLetterView(0, 0).setF();
        getLetterView(0, 3).setF();
        getLetterView(1, 4).setF();
        getLetterView(3, 1).setF();
        getLetterView(3, 3).setF();
        getLetterView(3, 5).setF();
        getLetterView(5, 0).setF();
        getLetterView(5, 3).setF();
        getLetterView(6, 4).setF();
        getLetterView(8, 1).setF();
        getLetterView(8, 3).setF();
        getLetterView(8, 5).setF();

        fadeOutView(grids);
        fadeInView(letters);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final HintHighlighter pulsateF = new HintHighlighter(getActivity());
                register(pulsateF);

                pulsateF.addPulsingTarget(getLetterView(3, 1), getLetterView(3, 1).getWidth() / 2);
                pulsateF.addTarget(progressBar);
                pulsateF.show();

                final HintPointer tapThisFHint = new HintPointer(getActivity(), getLetterView(3, 1), true, false);
                register(tapThisFHint);

                tapThisFHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf1));
                tapThisFHint.show();

                View.OnTouchListener listener = new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        int action = event.getAction();
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                pulsateF.dismiss();
                                tapThisFHint.dismiss();
                                incrementProgress();

                                final HintPointer tapAllFsHint = new HintPointer(getActivity(), letterLayout, true);
                                register(tapAllFsHint);

                                tapAllFsHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf2));
                                tapAllFsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        tapAllFsHint.dismiss();
                                        setupLetterStop();
                                    }
                                });
                                tapAllFsHint.getShadow().addTarget(progressBar);
                                tapAllFsHint.show();
                        }
                        return false;
                    }
                };
                getLetterView(3, 1).setOnTouchListener(listener);

            }
        }, 500);
    }

    // Responds to letter that are tapped, changes their color
    private void setupLetterStop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Run when the user has exceeded the given time to tap Fs
                // Displays a popup and advances to setSecondItemLayout()

                incrementProgress();
                disableLetters();

                final HintPointer niceWorkHint = new HintPointer(getActivity(), letterLayout, true);
                register(niceWorkHint);

                niceWorkHint.getShadow().addTarget(progressBar);
                niceWorkHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf3));
                niceWorkHint.addButton(ViewUtil.getString(R.string.button_next), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        niceWorkHint.dismiss();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fadeOutView(letters);
                                setSecondGridLayout();
                            }
                        }, 600);
                    }
                });
                niceWorkHint.show();
            }
        }, 8000);
    }


    private void setSecondGridLayout() {
        gridTextView.setVisibility(View.VISIBLE);
        getGridView(3, 0).removeImage();
        getGridView(1, 1).removeImage();
        getGridView(2, 3).removeImage();
        fadeInView(grids);

        final HintPointer secondItemsHint = new HintPointer(getActivity(), gridLayout, true);
        register(secondItemsHint);

        secondItemsHint.setText("In part three, place each item in its location from part one.");
        secondItemsHint.getShadow().addTarget(progressBar);
        secondItemsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secondItemsHint.dismiss();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setGridRecall();
                    }
                }, 600);
            }
        });
        secondItemsHint.show();
    }


    // Displays the same items as setInitialItemLayout()
    // Displays a new hint
    private void setSecondItemLayout() {
        gridTextView.setVisibility(View.VISIBLE);
        fadeInView(grids);

        final HintPointer secondItemsHint = new HintPointer(getActivity(), gridLayout, true);
        register(secondItemsHint);

        secondItemsHint.setText("In part three, place each item in its location from part one.");
        secondItemsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secondItemsHint.dismiss();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        fadeOutView(grids);
                        setGridRecall();
                    }
                }, 600);
            }
        });
        secondItemsHint.show();
    }

    // Displays the grid recall test and associated hints/prompts
    private void setGridRecall() {

        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(true);
            view.setListener(defaultListener);
        }

        remindMeHint.setText(ViewUtil.getString(R.string.popup_tutorial_needhelp));
        remindMeHint.addButton(ViewUtil.getString(R.string.popup_tutorial_remindme), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindMeHint.dismiss();
                remindMeHighlight.dismiss();
                remindMeHighlight.clearTargets();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        remindMeHighlights();
                    }
                }, 600);
            }
        });

        handler.post(firstRecallStepRunnable);
    }

    private void disableGrids(Grid2BoxView exemption) {
        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(false);
        }
        if (exemption != null) {
            exemption.setSelectable(true);
        }
    }

    private void enableGrids() {
        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(true);
        }
    }

    private void disableLetters() {
        int size = letterLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2LetterView view = (Grid2LetterView) letterLayout.getChildAt(i);
            view.setEnabled(false);
        }
    }

    private int determinePointerPosition(Grid2BoxView view) {
        int gridBoxHeight = view.getHeight();
        int[] gridBoxLocation = new int[2];
        view.getLocationOnScreen(gridBoxLocation);

        if (gridBoxLocation[1] < ((2 * gridBoxHeight) + progressBar.getHeight())) {
            // if grid box is in the first two rows of the grid, dialog appears below grid box
            return PointerDrawable.POINTER_BELOW;
        } else {
            return PointerDrawable.POINTER_ABOVE;
        }
    }


    private void removeSelection(@DrawableRes int id) {
        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if (!view.isSelected()) {
                continue;
            }
            int image = view.getImage();
            if (image == id) {
                view.removeImage();
                view.setSelected(false);
                return;
            }
        }
    }

    private void updateSelections() {
        phoneSelected = false;
        keySelected = false;
        penSelected = false;

        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if (!view.isSelected()) {
                continue;
            }
            int id = view.getImage();
            if (id == R.drawable.phone) {
                phoneSelected = true;
            }
            if (id == R.drawable.key) {
                keySelected = true;
            }
            if (id == R.drawable.pen) {
                penSelected = true;
            }
        }
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        if (phoneSelected && keySelected && penSelected) {
            if (otherItemsHint != null) {
                otherItemsHint.setVisibility(View.INVISIBLE);
                otherItemsHint.dismiss();
            }
            continueButton.setVisibility(View.VISIBLE);
            gridHintTextView.setVisibility(View.INVISIBLE);
        } else {
            gridHintTextView.setVisibility(View.VISIBLE);
            continueButton.setVisibility(View.GONE);
        }
    }

    // Determines which items to highlight for the remind me hints in the grid recall
    private void remindMeHighlights() {
        int targetCount = 0;
        final HintHighlighter remindMeTapHighlight = new HintHighlighter(getActivity());
        register(remindMeTapHighlight);

        remindMeTapHighlight.addTarget(progressBar);

        if (!phoneSelected) {
            targetCount++;
            Grid2BoxView phone = getGridView(1, 1);
            remindMeTapHighlight.addPulsingTarget(phone, 8);
        }
        if (!penSelected) {
            targetCount++;
            Grid2BoxView pen = getGridView(3, 0);
            remindMeTapHighlight.addPulsingTarget(pen, 8);
        }
        if (!keySelected) {
            targetCount++;
            Grid2BoxView key = getGridView(2, 3);
            remindMeTapHighlight.addPulsingTarget(key, 8);
        }
        if (targetCount > 0) {
            boxViewListener = new Grid2BoxView.Listener() {
                @Override
                public void onSelected(Grid2BoxView view) {
                    remindMeTapHighlight.dismiss();
                }
            };
            remindMeTapHighlight.show();
        }
    }

    Grid2BoxView.Listener defaultListener = new Grid2BoxView.Listener() {
        @Override
        public void onSelected(final Grid2BoxView view) {
            handler.removeCallbacks(remindMeRunnable);
            if (mechanicsHint != null) {
                mechanicsHint.dismiss();
            }

            boolean deselecting =
                    (view.getImage() == R.drawable.phone && phoneSelected) ||
                            (view.getImage() == R.drawable.key && keySelected) ||
                            (view.getImage() == R.drawable.pen && penSelected);

            if (!deselecting && phoneSelected && keySelected && penSelected) {
                userMovedOrRemoved = true;
            }

            if (dialog != null) {
                if (dialog.isAttachedToWindow()) {
                    dialog.dismiss();
                    if (view.getImage() == 0) {
                        view.setSelected(false);
                    }
                    handler.postDelayed(remindMeRunnable, 20000);
                    enableGrids();
                    return;
                }
            }

            disableGrids(view);

            int pointerPosition = determinePointerPosition(view);
            view.setSelected(true);

            dialog = new Grid2ChoiceDialog(
                    getActivity(),
                    view,
                    pointerPosition);

            dialog.setAnimationDuration(50);

            if (view.getImage() != 0) {
                dialog.disableChoice(view.getImage());
            }

            dialog.setListener(new Grid2ChoiceDialog.Listener() {
                @Override
                public void onSelected(int image) {
                    removeSelection(image);
                    view.setImage(image);
                    updateSelections();
                    if (dialogListener != null) {
                        dialogListener.onSelected(image);
                        dialogListener = null;
                    }
                    if (othersReady && !(phoneSelected && penSelected && keySelected)) {
                        handler.postDelayed(remindMeRunnable, 20000);
                    }
                    enableGrids();

                }

                @Override
                public void onRemove() {
                    userMovedOrRemoved = true;

                    view.removeImage();
                    view.setSelected(false);
                    updateSelections();
                    if (dialogListener != null) {
                        dialogListener.onRemove();
                        dialogListener = null;
                    }
                    if (othersReady && !(phoneSelected && penSelected && keySelected)) {
                        handler.postDelayed(remindMeRunnable, 20000);
                    }
                    enableGrids();
                }
            });

            dialog.show();

            if (deselecting) {
                checkForMechanicsHint();
            }

            if (boxViewListener != null) {
                boxViewListener.onSelected(view);
                boxViewListener = null;
            }
        }
    };

    Runnable remindMeRunnable = new Runnable() {
        @Override
        public void run() {
            if (otherItemsHint != null) {
                otherItemsHint.dismiss();
            }
            remindMeHighlight.addTarget(progressBar);
            remindMeHighlight.show();
            remindMeHint.show();
        }
    };

    Runnable firstRecallStepRunnable = new Runnable() {
        @Override
        public void run() {
            final Grid2BoxView boxView = getGridView(1, 1);

            final HintPointer recallHint = new HintPointer(getActivity(), getGridView(1, 1), true, false);
            register(recallHint);

            final HintPointer choiceSelectHint = new HintPointer(getActivity(), gridTextView);
            register(choiceSelectHint);

            final HintHighlighter pulsateGridItem = new HintHighlighter(getActivity());
            register(pulsateGridItem);

            final HintHighlighter pulsatePhone = new HintHighlighter(getActivity());
            register(pulsatePhone);

            boxViewListener = new Grid2BoxView.Listener() {
                @Override
                public void onSelected(Grid2BoxView view) {
                    view.setSelectable(false);
                    if (pulsateGridItem != null) {
                        pulsateGridItem.dismiss();
                    }
                    if (recallHint != null) {
                        recallHint.dismiss();
                    }
                    dialogListener = new Grid2ChoiceDialog.Listener() {
                        @Override
                        public void onSelected(int image) {
                            choiceSelectHint.dismiss();
                            pulsatePhone.dismiss();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    enableGrids();

                                    otherItemsHint = new HintPointer(getActivity(), gridTextView);
                                    register(otherItemsHint);

                                    otherItemsHint.setText("Great! Now, place the other two items on the grid.");
                                    otherItemsHint.show();
                                    othersReady = true;
                                    handler.postDelayed(remindMeRunnable, 20000);

                                }
                            }, 300);

                        }

                        @Override
                        public void onRemove() {
                            userMovedOrRemoved = true;
                        }
                    };


                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.getPenView().setOnTouchListener(null);
                            dialog.getKeyView().setOnTouchListener(null);
                            pulsatePhone.addTarget(progressBar);
                            pulsatePhone.addTarget(dialog, 8, 16);
                            pulsatePhone.addTarget(boxView, 8, 16);
                            pulsatePhone.addPulsingTarget(dialog.getPhoneView(), 8);
                            pulsatePhone.show();

                            choiceSelectHint.setText("Now, tap the cell phone to place it in the selected box.");
                            choiceSelectHint.show();

                        }
                    }, 500);

                }
            };
            recallHint.setText("<b>Hint:</b> The cell phone was located here. Tap this box.");
            pulsateGridItem.addPulsingTarget(boxView, 8);
            pulsateGridItem.addTarget(progressBar);
            pulsateGridItem.show();
            recallHint.show();
        }
    };

    Runnable choiceRemoveRunnable = new Runnable() {
        @Override
        public void run() {
            dialog.getPhoneView().setOnTouchListener(null);
            dialog.getPenView().setOnTouchListener(null);
            dialog.getKeyView().setOnTouchListener(null);

            final HintPointer choiceRemoveHint = new HintPointer(getActivity(), gridTextView, false, true);
            register(choiceRemoveHint);

            final HintHighlighter choiceRemoveHighlight = new HintHighlighter(getActivity());
            register(choiceRemoveHighlight);

            dialogListener = new Grid2ChoiceDialog.Listener() {
                @Override
                public void onSelected(int image) {

                }

                @Override
                public void onRemove() {
                    choiceRemoveHighlight.dismiss();
                    choiceRemoveHint.dismiss();

                    final HintPointer choiceAgainHint = new HintPointer(getActivity(), gridTextView, false, true);
                    register(choiceAgainHint);

                    final HintHighlighter choiceAgainHighlight = new HintHighlighter(getActivity());
                    register(choiceAgainHighlight);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            defaultListener.onSelected(getGridView(1, 1));
                            getGridView(1, 1).setSelected(true);
                            getGridView(1, 1).setSelectable(false);

                            dialogListener = new Grid2ChoiceDialog.Listener() {
                                @Override
                                public void onSelected(int image) {
                                    getGridView(1, 1).setSelectable(true);
                                    choiceAgainHighlight.dismiss();
                                    choiceAgainHint.dismiss();

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            enableGrids();

                                            otherItemsHint = new HintPointer(getActivity(), gridTextView);
                                            register(otherItemsHint);

                                            otherItemsHint.setText("Now, place the other two items on the grid.");
                                            otherItemsHint.show();
                                            othersReady = true;
                                            handler.postDelayed(remindMeRunnable, 20000);

                                        }
                                    }, 300);
                                }

                                @Override
                                public void onRemove() {
                                    userMovedOrRemoved = true;
                                }
                            };
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    dialog.getPenView().setOnTouchListener(null);
                                    dialog.getKeyView().setOnTouchListener(null);

                                    choiceAgainHighlight.addTarget(progressBar);
                                    choiceAgainHighlight.addTarget(dialog, 8, 16);
                                    choiceAgainHighlight.addTarget(getGridView(1, 1), 8, 16);
                                    choiceAgainHighlight.addPulsingTarget(dialog.getPhoneView(), 8);
                                    choiceAgainHighlight.show();

                                    choiceAgainHint.setText("Great! Let's place the cell phone back in the first box");
                                    choiceAgainHint.show();
                                }
                            }, 500);
                        }
                    }, 300);
                }
            };

            choiceRemoveHighlight.addTarget(dialog.getRemoveItemView(), 8, 8);
            choiceRemoveHighlight.show();

            choiceRemoveHint.setText("Tap Remove Item button");
            choiceRemoveHint.show();
        }
    };

    private void checkForMechanicsHint() {
        if (!mechanicsHintShown) {
            mechanicsHintShown = true;

            if (otherItemsHint != null) {
                otherItemsHint.dismiss();
            }

            mechanicsHint = new HintPointer(getActivity(), gridTextView);
            mechanicsHint.setText("Change your mind? Select a different item to replace, or tap <b>Remove Item</b> to clear.");
            mechanicsHint.addButton(ViewUtil.getString(R.string.popup_gotit), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mechanicsHint.dismiss();
                }
            });
            register(mechanicsHint);
            mechanicsHint.show();
        }
    }

    private void launchMechanicsTutorial() {
        handler.removeCallbacks(remindMeRunnable);
        gridHintTextView.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.GONE);

        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.removeImage();
            view.setSelected(false);
        }

        getGridView(1, 1).setSelected(true);
        getGridView(1, 1).setImage(R.drawable.phone);

        final HintPointer hint = new HintPointer(getActivity(), gridLayout, true);
        hint.setText("<b>Nice work!</b><br>It looks like you didn’t need any help with placement, but we want to make sure we still teach you how to swap and remove items before you move onto the actual test.");
        hint.addButton("Show Me", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hint.dismiss();
                mechanicsTutorial1();
            }
        });
        register(hint);
        hint.show();

    }

    private void mechanicsTutorial1() {
        disableGrids(null);
        final HintPointer hint = new HintPointer(getActivity(), getGridView(4, 2), true);
        hint.setText("If you change your mind after placing an item, you can move it. Let’s try it.");
        hint.addButton(ViewUtil.getString(R.string.button_okay), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hint.dismiss();
                mechanicsTutorial2();
            }
        });
        int padding = ViewUtil.pxToDp((int) (1.75 * getGridView(2, 2).getWidth()));
        hint.getShadow().addTarget(getGridView(2, 2), 8, padding);
        hint.getShadow().addTarget(progressBar);
        register(hint);
        hint.show();
    }

    private void mechanicsTutorial2() {
        final Grid2BoxView boxView = getGridView(1, 3);
        final HintPointer hint = new HintPointer(getActivity(), boxView, true, false, true);
        hint.setText("First, tap a different box.");
        hint.getShadow().addPulsingTarget(boxView, 8);
        hint.getShadow().addTarget(progressBar);
        register(hint);

        boxView.setListener(new Grid2BoxView.Listener() {
            @Override
            public void onSelected(Grid2BoxView view) {
                hint.dismiss();
                view.setSelected(true);
                view.setSelectable(false);

                int pointerPosition = determinePointerPosition(view);

                dialog = new Grid2ChoiceDialog(
                        getActivity(),
                        view,
                        pointerPosition);
                dialog.setAnimationDuration(50);
                dialog.getPenView().setOnTouchListener(null);
                dialog.getKeyView().setOnTouchListener(null);

                dialog.setListener(new Grid2ChoiceDialog.Listener() {
                    @Override
                    public void onSelected(int image) {
                        removeSelection(image);
                        boxView.setImage(image);
                        if(mechanicsHint!=null){
                            mechanicsHint.dismiss();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mechanicsTutorial3();
                            }
                        },300);
                    }

                    @Override
                    public void onRemove() {

                    }
                });
                dialog.show();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mechanicsHint = new HintPointer(getActivity(), gridTextView, true);
                        mechanicsHint.setText("Then, tap the cell phone button to place it in the new box.");

                        mechanicsHint.getShadow().addTarget(dialog, 8, 8);
                        mechanicsHint.getShadow().addTarget(progressBar);
                        mechanicsHint.getShadow().addTarget(getGridView(1, 3),8,8);
                        mechanicsHint.getShadow().addPulsingTarget(dialog.getPhoneView(), 8);

                        register(mechanicsHint);
                        mechanicsHint.show();
                    }
                }, 300);

            }
        });
        hint.show();

        boxView.setSelectable(true);
    }

    private void mechanicsTutorial3() {
        final Grid2BoxView boxView = getGridView(1, 3);
        boxView.setSelectable(true);

        final HintPointer hintGreat = new HintPointer(getActivity(), boxView, true, false, true);
        hintGreat.setText("Great! If you would like to an item from a box, first tap that box...");
        hintGreat.getShadow().addPulsingTarget(boxView, 8);
        hintGreat.getShadow().addTarget(progressBar);
        register(hintGreat);

        boxView.setListener(new Grid2BoxView.Listener() {
            @Override
            public void onSelected(Grid2BoxView view) {
                hintGreat.dismiss();
                view.setSelected(true);
                view.setSelectable(false);

                int pointerPosition = determinePointerPosition(view);

                dialog = new Grid2ChoiceDialog(
                        getActivity(),
                        view,
                        pointerPosition);
                dialog.setAnimationDuration(50);
                dialog.disableChoice(R.drawable.phone);
                dialog.getPenView().setOnTouchListener(null);
                dialog.getKeyView().setOnTouchListener(null);

                dialog.show();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        TextView textView = dialog.getRemoveItemView();

                        final HintPointer hint = new HintPointer(getActivity(), textView, true,false,true);
                        hint.setText("...then tap <b>Remove Item</b>.");

                        hint.getShadow().addTarget(textView, 8, 8);
                        hint.getShadow().addTarget(progressBar);

                        dialog.setListener(new Grid2ChoiceDialog.Listener() {
                            @Override
                            public void onSelected(int image) {

                            }

                            @Override
                            public void onRemove() {
                                hint.setVisibility(View.INVISIBLE);
                                hint.dismiss();
                                mechanicsTutorial4();
                            }
                        });

                        register(hint);
                        hint.show();
                    }
                }, 500);

            }
        });
        hintGreat.show();
    }

    private void mechanicsTutorial4() {
        final Grid2BoxView boxView = getGridView(1, 3);
        boxView.setSelected(false);
        boxView.removeImage();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final HintPointer hint = new HintPointer(getActivity(),gridTextView);
                hint.setText("Perfect! You've got it.");
                hint.addButton("Finish Tutorial", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hint.setVisibility(View.INVISIBLE);
                        hint.dismiss();
                        incrementProgress();
                        fadeOutView(grids);
                        showComplete();
                    }
                });
                register(hint);
                hint.show();
            }
        },500);
    }

}