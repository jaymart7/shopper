import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import component.HomeComponent
import component.HomeEvent
import model.Product
import model.presentation.Account
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.ViewState

@Composable
internal fun HomeContent(
    component: HomeComponent,
    modifier: Modifier = Modifier
) {
    val model by component.model.subscribeAsState()

    HomeContent(
        onEvent = { component.handleEvent(it) },
        model = model,
        modifier = modifier
    )
}

@Composable
private fun HomeContent(
    onEvent: (HomeEvent) -> Unit,
    model: HomeComponent.Model,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AccountContent(
            onRefresh = { onEvent(HomeEvent.RefreshAccount) },
            onLogout = { onEvent(HomeEvent.Logout) },
            accountState = model.accountState
        )

        HorizontalDivider()

        ProductContent(
            onItemClick = { onEvent(HomeEvent.ProductClick(it)) },
            onRefresh = { onEvent(HomeEvent.RefreshProduct) },
            productsState = model.productsState,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AccountContent(
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    accountState: ViewState<Account>,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        when (accountState) {
            is ViewState.Error -> {
                Text(
                    accountState.error.message.orEmpty(),
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )

                Button(
                    onClick = onRefresh,
                    content = {
                        Text("Retry")
                    }
                )
            }

            is ViewState.Loading -> CircularProgressIndicator()

            is ViewState.Success -> {
                Text(
                    text = accountState.data.name,
                    maxLines = 1
                )

                OutlinedButton(
                    onClick = onLogout,
                    content = {
                        Text("Logout")
                    }
                )
            }
        }
    }
}

@Composable
fun ProductContent(
    onItemClick: (Product) -> Unit,
    onRefresh: () -> Unit,
    productsState: ViewState<List<Product>>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (productsState) {
            is ViewState.Error -> {
                Text(
                    productsState.error.message.orEmpty(),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onRefresh,
                    content = {
                        Text("Refresh")
                    }
                )
            }

            is ViewState.Loading -> CircularProgressIndicator()

            is ViewState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(
                        productsState.data,
                        key = { it.id },
                        itemContent = {
                            ProductItem(
                                onClick = { onItemClick(it) },
                                product = it
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductItem(
    onClick: () -> Unit,
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(product.title)
        }
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    val account = Account(name = "Name", username = "user name")

    HomeContent(
        onEvent = {},
        model = HomeComponent.Model(
            accountState = ViewState.Success(account),
            productsState = ViewState.Success(
                listOf(
                    Product(1, "Product 1"),
                    Product(2, "Product 2"),
                    Product(3, "Product 3")
                )
            )
        )
    )
}