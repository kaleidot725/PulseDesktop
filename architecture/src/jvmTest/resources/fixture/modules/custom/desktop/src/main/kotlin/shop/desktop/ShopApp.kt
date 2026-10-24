package shop.desktop

import shop.catalog.CatalogScreen
import shop.data.TableItemRepository
import shop.domain.ListItemsUseCase

val app = CatalogScreen(ListItemsUseCase(TableItemRepository()))
