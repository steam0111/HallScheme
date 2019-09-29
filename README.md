# Library for draw hallScheme

[![](https://jitpack.io/v/steam0111/HallScheme.svg)](https://jitpack.io/#steam0111/HallScheme)

add the library to your module `build.gradle`
```gradle
dependencies {
    implementation 'com.github.steam0111:HallScheme:0.5.3'
}
```

define view in xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="#FFD8E2E6">

    <com.itrocket.hallschemelibrary.SeatPlanView
            android:id="@+id/seatPlanView"
            android:layout_centerHorizontal="true"
            android:layout_centerVertical="true"
            android:layout_width="match_parent"
            android:background="@android:color/holo_blue_bright"
            android:layout_height="280dp" />

    <Button android:id="@+id/btnGetClickedCount"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="get clicked count"/>
</RelativeLayout>
```

Define how to draw seat : 
```kotlin
//If you want to draw drawable
class DrawableClickableSeat(var drawableResId : Int, //pass drawable res off
                            var drawableOnResId : Int) //pass drawable res on
//inherit from BaseSeat
    : BaseSeat() {

    //draw your own seat as you want
    override fun drawSeat(
        canvas: Canvas,
        pointLeftTop: Point,
        pointRightBottom: Point,
        seatStatus: SeatStatus,
        seatPlanView: SeatPlanView
    ) {

        //get drawable from resources
        val drawDrawable = if (seatStatus == SeatStatus.CLICKED) {
            seatPlanView.context.getDrawable(drawableResId)
        } else {
            seatPlanView.context.getDrawable(drawableOnResId)
        }

        //can use some support function from SeatPlanView
        SeatPlanView
            .drawDrawable(
                canvas, drawDrawable, pointLeftTop, pointRightBottom
            )
            
        //if you wanna draw text over cell
        val textCy = pointRightBottom.y - ((pointRightBottom.y - pointLeftTop.y) / 2)
        val yPos =
            (textCy - (SeatPlanView.textPaint.descent() + SeatPlanView.textPaint.ascent()) / 2)

        SeatPlanView.textPaint.color = Color.WHITE

        canvas.drawText(
            "1",
            (pointRightBottom.x - ((pointRightBottom.x - pointLeftTop.x) / 2).toFloat()),
            yPos,
            SeatPlanView.textPaint
        )
    }
}

//If you want to draw text
class TextSeat(
    var text : String,
    @ColorInt val color : Int
) : BaseSeat() {
    override fun drawSeat(canvas: Canvas,
                          pointLeftTop: Point,
                          pointRightBottom: Point,
                          seatStatus: SeatStatus,
                          seatPlanView: SeatPlanView) {
        val cx = pointLeftTop.x + (pointRightBottom.x - pointLeftTop.x)/2
        val cy =  pointLeftTop.y + (pointRightBottom.y - pointLeftTop.y)/2
        SeatPlanView.drawTextCentred(text, canvas, color, cx.toFloat(), cy.toFloat())
    }
}
```
Use 
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetClickedCount.setOnClickListener {
            Toast.makeText(this, seatPlanView.getClickedSeats().size.toString(), Toast.LENGTH_SHORT).show()
        }
        
        // call drawSeatPlan
        // full code in repo
         seatPlanView.drawSeatPlan(
            getSeatData(), // make data for seat
            true, //define zoomable or not
            getLegendData(), //make data for legend
            clickedRuleForClickableItems = { seat, seats ->
                seatValidationRules(seat, seats) //set validation rules for click on seats
            },
            seatGap = 1, //seat gap between chairs
            screen = Screen(Color.RED, Color.BLUE, 30f, "Screen") //add screen
        )
    }
}
```

Result of code above : 

<img src = "https://github.com/steam0111/HallScheme/blob/master/screenshot1.png" width = "320"/>

Full source code : 

https://github.com/steam0111/HallScheme/tree/master/app
