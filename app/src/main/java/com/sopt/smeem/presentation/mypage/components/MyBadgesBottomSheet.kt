package com.sopt.smeem.presentation.mypage.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sopt.smeem.R
import com.sopt.smeem.data.datasource.BadgeList
import com.sopt.smeem.domain.dto.GetBadgeListDto
import com.sopt.smeem.domain.model.BadgeType
import com.sopt.smeem.presentation.compose.components.SmeemBottomSheet
import com.sopt.smeem.presentation.compose.theme.Typography
import com.sopt.smeem.presentation.compose.theme.black
import com.sopt.smeem.presentation.compose.theme.gray100
import com.sopt.smeem.presentation.compose.theme.gray500
import com.sopt.smeem.presentation.compose.theme.point
import com.sopt.smeem.util.VerticalSpacer
import com.sopt.smeem.util.previewPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBadgesBottomSheet(
    badge: GetBadgeListDto,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    SmeemBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        if (badge.hasBadge) {
            ObtainedBottomSheetContent(info = badge)
        } else {
            NotObtainedBottomSheetContent(info = badge)
        }
    }
}

@Composable
fun ObtainedBottomSheetContent(
    info: GetBadgeListDto,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(info.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = previewPlaceholder(image = R.drawable.ic_badge_welcome),
            modifier = Modifier
                .widthIn(max = 120.dp)
                .aspectRatio(1f)
        )
        VerticalSpacer(height = 26.dp)

        Text(
            text = info.name,
            style = Typography.headlineSmall,
            color = black
        )

        VerticalSpacer(height = 8.dp)

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = info.contentForBadgeOwner ?: "",
            style = Typography.bodyMedium,
            color = black,
            textAlign = TextAlign.Center
        )

        VerticalSpacer(height = 12.dp)

        Text(
            text = info.getAcquistionText(),
            style = Typography.bodyMedium,
            color = gray500
        )
    }

}

@Composable
fun NotObtainedBottomSheetContent(
    info: GetBadgeListDto,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = modifier
                .widthIn(max = 120.dp)
                .aspectRatio(1f),
            colors = CardDefaults.cardColors(
                containerColor = gray100
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_badge_lock),
                    contentDescription = null
                )
            }
        }

        VerticalSpacer(height = 26.dp)

        Text(
            text = info.name,
            style = Typography.headlineSmall,
            color = black
        )

        if (info.type!! == BadgeType.COUNTING) {
            val parts = info.getNonBadgeTextParts()

            VerticalSpacer(height = 8.dp)

            Row {
                Text(
                    text = parts[0],
                    style = Typography.bodyMedium,
                    color = black
                )
                Text(
                    text = parts[1],
                    style = Typography.bodyLarge,
                    color = point
                )
                Text(
                    text = parts[2],
                    style = Typography.bodyMedium,
                    color = black
                )
            }
        } else {
            VerticalSpacer(height = 8.dp)

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = info.contentForNonBadgeOwner ?: "",
                style = Typography.bodyMedium,
                color = black
            )
        }

        VerticalSpacer(height = 12.dp)

        Text(
            text = info.getAcquistionText(),
            style = Typography.bodyMedium,
            color = gray500
        )
    }

}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ObtainedBottomSheetContentPreview() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ObtainedBottomSheetContent(
            info = BadgeList.sprint2.first(),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun NotObtainedBottomSheetContentPreview() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NotObtainedBottomSheetContent(
            info = BadgeList.sprint2[1],
        )
    }
}