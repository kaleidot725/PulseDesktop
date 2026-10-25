package shop.desktop

import shop.catalog.CatalogScreen
import shop.data.repository.TableItemRepository
import shop.domain.usecase.ListItemsUseCase

val app = CatalogScreen(ListItemsUseCase(TableItemRepository()))
