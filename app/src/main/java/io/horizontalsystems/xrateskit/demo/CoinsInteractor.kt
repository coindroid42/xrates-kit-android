package io.horizontalsystems.xrateskit.demo

import io.horizontalsystems.xrateskit.entities.ChartInfo
import io.horizontalsystems.xrateskit.entities.ChartType
import io.horizontalsystems.xrateskit.entities.MarketInfo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CoinsInteractor(private val ratesManager: RatesManager) {
    var presenter: CoinsPresenter? = null

    private var marketInfoDisposables = CompositeDisposable()
    private var chartInfoDisposables = CompositeDisposable()

    fun set(coins: List<String>) {
        ratesManager.set(coins)
    }

    fun set(currency: String) {
        ratesManager.set(currency)
    }

    fun refresh() {
        ratesManager.refresh()
    }

    fun subscribeToMarketInfo(currency: String) {
        marketInfoDisposables.clear()

        ratesManager.marketInfoObservable(currency)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    presenter?.onUpdateMarketInfo(it)
                }.let {
                    marketInfoDisposables.add(it)
                }
    }

    fun subscribeToChartInfo(coins: List<String>, currency: String) {
        chartInfoDisposables.clear()

        coins.forEach { coin ->
            ratesManager.chartInfoObservable(coin, currency, ChartType.DAILY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({
                        presenter?.onUpdateChartInfo(it, coin)
                    }, {
                        presenter?.onFailChartInfo(coin)
                    }).let {
                        chartInfoDisposables.add(it)
                    }
        }
    }


    fun marketInfo(coin: String, currency: String): MarketInfo? {
        return ratesManager.marketInfo(coin, currency)
    }

    fun chartInfo(coin: String, currency: String): ChartInfo? {
        return ratesManager.chartInfo(coin, currency, ChartType.DAILY)
    }
}
