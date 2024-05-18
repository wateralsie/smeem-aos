package com.sopt.smeem.presentation.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sopt.smeem.presentation.compose.theme.Typography
import com.sopt.smeem.presentation.compose.theme.black
import com.sopt.smeem.presentation.compose.theme.point
import com.sopt.smeem.presentation.compose.theme.white
import com.sopt.smeem.util.HorizontalSpacer
import com.sopt.smeem.util.VerticalSpacer

@Composable
fun SmeemTimePickerDialog(
    setShowDialog: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveButtonClick: (Int, Int) -> Unit,
    initialHour: Int,
    initialMinute: Int,
    modifier: Modifier = Modifier,
) {

    val timeOfDay = remember { listOf("오전", "오후") }
    val hour = remember { (1..12).map { it.toString() } }
    val minute = remember { listOf("00", "30") }

    val timeOfDayPickerState = rememberPickerState("오전")
    val hourPickerState = rememberPickerState("1")
    val minutePickerState = rememberPickerState("00")

    val selectedTimeOfDay = timeOfDayPickerState.selectedItem
    val selectedHour = hourPickerState.selectedItem.toInt()
    val selectedMinute = minutePickerState.selectedItem.toInt()

    val selectedHour24 = if (selectedTimeOfDay == "오후") {
        if (selectedHour == 12) selectedHour else selectedHour + 12
    } else {
        if (selectedHour == 12) 0 else selectedHour
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(10.dp),
            color = white
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 30.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    verticalAlignment = Alignment.Top
                ) {

                    Picker(
                        state = timeOfDayPickerState,
                        items = timeOfDay,
                        visibleItemsCount = 3,
                        startIndex = if (initialHour < 12 || initialHour == 24) 0 else 1,
                        textModifier = Modifier.padding(18.dp),
                        modifier = Modifier.weight(1f),
                        textStyle = Typography.titleSmall.copy(color = black),
                        dividerColor = point,
                    )

                    HorizontalSpacer(width = 24.dp)

                    Picker(
                        state = hourPickerState,
                        items = hour,
                        visibleItemsCount = 3,
                        startIndex = if (initialHour <= 12) initialHour - 1 else initialHour - 13,
                        textModifier = Modifier.padding(18.dp),
                        modifier = Modifier
                            .weight(1f)
                            .alignBy { it.measuredHeight / 2 },
                        textStyle = Typography.titleSmall.copy(color = black),
                        dividerColor = point,
                    )

                    HorizontalSpacer(width = 12.dp)

                    Text(
                        text = ":",
                        style = Typography.titleSmall,
                        color = Color.Black,
                        modifier = Modifier.alignBy { it.measuredHeight / 2 }

                    )

                    HorizontalSpacer(width = 12.dp)

                    Picker(
                        state = minutePickerState,
                        items = minute,
                        visibleItemsCount = 3,
                        startIndex = if (initialMinute == 0) 0 else 1,
                        textModifier = Modifier.padding(18.dp),
                        modifier = Modifier.weight(1f),
                        textStyle = Typography.titleSmall.copy(color = black),
                        dividerColor = point,
                    )
                }

                VerticalSpacer(height = 43.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 24.dp, bottom = 24.dp)
                ) {

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = { setShowDialog(false) },
                    ) {
                        Text(
                            text = "취소",
                            style = Typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = point
                        )
                    }

                    HorizontalSpacer(width = 8.dp)

                    TextButton(
                        onClick = { onSaveButtonClick(selectedHour24, selectedMinute) },
                    ) {
                        Text(
                            text = "저장",
                            style = Typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = point
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSmeemTimePickerDialog() {
    SmeemTimePickerDialog(
        setShowDialog = {},
        onSaveButtonClick = { _, _ -> },
        initialHour = 13,
        initialMinute = 30,
        onDismissRequest = {}
    )
}