package shop.desktop

import shop.catalog.CatalogScreen
import shop.db.ItemTable
import shop.repository.TableItemRepository
import shop.usecase.ListItemsUseCase

val app = CatalogScreen(ListItemsUseCase(TableItemRepository(ItemTable())))
