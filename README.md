# ExpandingProgressBar-android

<img src="https://i.imgur.com/H0bQBHa.gif" width="300"/>


## Basic usage 

(ConstraintLayout is recommended)

1. Add expandingprogressbar.megapho.com.expandingprogressbar.ExpandingProgressBar to your layout. Make sure to have width and height equal to match_parent or it's ConstraintLayout equivalent match_constraint(0dp)

        <expandingprogressbar.megapho.com.expandingprogressbar.ExpandingProgressBar android:id="@+id/epb"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="@color/background"

            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

2. Programmatically start ExpandingProgressBar whenever needed, and don't forget to set onExpandedListener if needed!

        final ExpandingProgressBar expandedProgressBar = findViewById(R.id.epb);
        expandedProgressBar.start();

        expandedProgressBar.setOnExpandListener(new ExpandingProgressBar.onExpandListener() {
            @Override
            public void preExpand() {
                
            }

            @Override
            public void postExpand() {

            }
        });

3. When the progressBar needs to start the expand animation just call

        expandedProgressBar.expand();

4. Extra steps! Customizing view
        
Customizable attributes:

  * radius: ProgressBar circle radius.
  * color: Color of progress bar and start of expand.
  * color_gradient: Second color of expand animation.
  * rotation_speed: Speed of rotation of circular progress bar.
  * stroke_width: Width of progress bar arches.
  * top_percentage: Sets top percentage from the top of progress bar.
  * gravity: Sets horizontal position of progress bar.

Example:

        <expandingprogressbar.megapho.com.expandingprogressbar.ExpandingProgressBar android:id="@+id/epb"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="@color/background"
            android:gravity="start"

            app:radius="50dp"
            app:color="#ffbd5b"
            app:color_gradient="#feab50"
            app:rotation_speed="7"
            app:stroke_width="4dp"
            app:top_percentage="0.4"

            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

If you want to add a view on top of the progress bar what I recommend is to add a guideline at roughly the same top_percentage as your ExpandingProgressBar, like so


    <android.support.constraint.Guideline android:id="@+id/guideline1"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"

        app:layout_constraintGuide_percent="0.77" />

    <expandingprogressbar.megapho.com.expandingprogressbar.ExpandingProgressBar android:id="@+id/epb"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="@color/background"

        app:top_percentage="0.8"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView android:id="@+id/tv_loading"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="20sp"

        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintBottom_toTopOf="@id/guideline1"

        android:text="@string/loading" />
