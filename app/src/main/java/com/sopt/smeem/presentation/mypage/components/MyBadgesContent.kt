package com.sopt.smeem.presentation.mypage.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sopt.smeem.R
import com.sopt.smeem.data.datasource.BadgeList
import com.sopt.smeem.domain.model.mypage.MyBadges
import com.sopt.smeem.presentation.home.calendar.ui.theme.SmeemTheme

@Composable
fun MyBadgesContent(
    modifier: Modifier = Modifier,
    badges: List<MyBadges>
) {
    SmeemContents(
        modifier = modifier,
        title = stringResource(R.string.my_badges)
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(7.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(badges) { badge ->
                if (badge.hasObtained) {
                    MyBadgesObtainedCard(info = badge)
                } else {
                    MyBadgesNotObtainedCard(info = badge)
                }
            }
        }
    }
}

@Composable
fun MyBadgesObtainedCard(
    info: MyBadges,
    modifier: Modifier = Modifier
) {

}

@Composable
fun MyBadgesNotObtainedCard(
    info: MyBadges,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
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
}

@Preview
@Composable
fun MyBadgesPreview() {
    SmeemTheme {
        MyBadgesContent(badges = BadgeList.sprint2)
    }
}

@Preview(widthDp = 200)
@Composable
fun MyBadgesNotObtainedCardPreview() {
    SmeemTheme {
        MyBadgesNotObtainedCard(info = BadgeList.sprint2[2])
    }
}