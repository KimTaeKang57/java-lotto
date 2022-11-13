package lotto;

import camp.nextstep.edu.missionutils.Console;

import java.util.ArrayList;
import java.util.List;

public class LottoApplication {
    private final String BONUS_BALL_RESULT_MESSAGE = "%d개 일치, 보너스 볼 일치 (%,d원) - %d개";
    private final String RESULT_MESSAGE = "%d개 일치 (%,d원) - %d개";
    private final String WINNING_NUMBER_INPUT = "당첨 번호를 입력해주세요.";
    private final String DUPLICATE_NUMBER_ERROR = "[ERROR] 로또 번호가 중복됩니다.";
    private final String MONEY_INPUT = "구입금액을 입력해 주세요.";
    private final String BONUS_NUMBER_INPUT = "보너스 번호를 입력해 주세요.";

    private RandomLotto randomLottos;
    private RandomLottoGenerator randomLottoGenerator;
    private WinningLotto winningLotto;

    private List<Lotto> randomNumbers;
    private int bonusNumber;

    public void run() {
        System.out.println(MONEY_INPUT);
        int money = Integer.parseInt(Console.readLine());
        if (money % 1000 != 0) {
            throw new IllegalArgumentException();
        }

        randomLottoGenerator = new RandomLottoGenerator();
        randomLottos = new RandomLotto(randomLottoGenerator);
        randomNumbers = randomLottos.makeRandomLottos(money);
        randomNumbers.forEach(random -> random.printNumbers());

        // 보너스 숫자

        // 로또 당첨번호
        winningLotto = new WinningLotto(inputAndGetLotto(), inputBonusNumber());

        // 총 상금
        int sum = 0;
        sum = getSum(sum);

        // 결과 출력
        printResult();

        // 수익률 출력
        printYield((double) money, (double) sum);
    }

    private int inputBonusNumber() {
        System.out.println(BONUS_NUMBER_INPUT);
        bonusNumber = Integer.parseInt(Console.readLine());
        return bonusNumber;
    }

    private Lotto inputAndGetLotto() {
        System.out.println(WINNING_NUMBER_INPUT);
        List<Integer> inputWinningLotto = new ArrayList<>();
        String[] split = Console.readLine().split(",");

        for (int idx = 0; idx < split.length; idx++) {
            if (inputWinningLotto.contains(Integer.parseInt(split[idx]))) {
                throw new IllegalArgumentException(DUPLICATE_NUMBER_ERROR);
            }
            inputWinningLotto.add(Integer.parseInt(split[idx]));
        }

        Lotto inputLotto = new Lotto(inputWinningLotto);

        return inputLotto;
    }

    private void printYield(double money, double sum) {
        double yield = sum / money * 100;
        System.out.println("총 수익률은 " + String.format("%.1f", yield) + "%입니다.");
    }

    private void printResult() {
        System.out.println("당첨 통계");
        System.out.println("---");
        for (LottoReward lottoReward : LottoReward.values()) {
            if (lottoReward == LottoReward.SECOND) {
                System.out.println(String.format(BONUS_BALL_RESULT_MESSAGE,
                        lottoReward.getMatchingNumbers(), lottoReward.getReward(), lottoReward.getCount()));
                continue;
            }
            System.out.println(String.format(RESULT_MESSAGE,
                    lottoReward.getMatchingNumbers(), lottoReward.getReward(), lottoReward.getCount()));
        }
    }

    private int getSum(int sum) {
        for (Lotto lotto : randomNumbers) {
            int cnt = winningLotto.matchCount(lotto);
            winningLotto.matchCount(lotto);

            if (cnt < 3) continue;

            /**
             * FIRST, SECOND, THIRD, FOURTH, FIFTH
             */
            LottoReward rank = LottoReward.getRank(cnt, isContainsBonusNumber(lotto, cnt));
            // 해당 rank 의 숫자를 더해준다
            rank.plusCount();
            // 해당 rank 의 상금을 더해준다
            sum += rank.getReward();
        }
        return sum;
    }

    private boolean isContainsBonusNumber(Lotto lotto, int cnt) {
        boolean containsBonusNumber = false;
        if (cnt == 5) {
            if (winningLotto.isContainsBonusNumber(lotto, bonusNumber)) {
                containsBonusNumber = true;
            }
        }
        return containsBonusNumber;
    }
}
